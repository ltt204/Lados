package org.nullgroup.lados.data.repositories.implementations.auth

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import org.nullgroup.lados.data.models.AuthTokens
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.data.repositories.interfaces.auth.AuthRepository
import org.nullgroup.lados.data.repositories.interfaces.common.SharedPreferencesRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import org.nullgroup.lados.viewmodels.common.states.ResourceState

class AuthRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferencesRepository,
    private val credentialManager: CredentialManager,
    private val credentialRequest: GetCredentialRequest,
) : AuthRepository {
    private fun isTokenExpired(idToken: String): Boolean {
        try {
            val parts = idToken.split(".")
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            val exp = json.getLong("exp")

            return System.currentTimeMillis() / 1000 > exp
        } catch (error: Exception) {
            return true
        }
    }

    private fun mapToUser(firebaseUser: FirebaseUser, provider: String, role: String?): User {
        return User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            role = role ?: UserRole.CUSTOMER.name,
            phoneNumber = firebaseUser.phoneNumber ?: "",
            avatarUri = firebaseUser.photoUrl?.toString() ?: "",
            provider = provider,
            isActive = true,
        )
    }

    override suspend fun signInWithGoogle(context: Context): ResourceState<User> {
        return try {
            val result =
                credentialManager.getCredential(context, credentialRequest)
            Log.d("SignIn", "Credential received")
            val credential = result.credential
            Log.d("SignIn", "Credential type: ${credential.type}")

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()

            if (authResult != null) {
                val userRef =
                    firestore.collection("users").document(authResult.user!!.uid).get().await()
                val user = userRef.toObject(User::class.java)

                if (user != null && !user.isActive) {
                    return ResourceState.Error("Account is disabled, please check your email for reset password")
                }

                val refreshToken = authResult.user!!.getIdToken(false).await().token ?: ""
                sharedPreferences.saveAuthTokens(
                    AuthTokens(
                        googleIdToken,
                        refreshToken,
                        "google.com"
                    )
                )

                val newUser = mapToUser(authResult.user!!, "google.com", UserRole.CUSTOMER.name)
                if (authResult.additionalUserInfo!!.isNewUser) {
                    userRepository.saveUserToFirestore(newUser)
                }

                ResourceState.Success(user)
            } else {
                ResourceState.Error("User is null")
            }
        } catch (error: Exception) {
            ResourceState.Error(error.message)
        }
    }

    override suspend fun autoSignIn(): ResourceState<User> {
        val tokens =
            sharedPreferences.getAuthTokens() ?: return ResourceState.Idle

        return try {
            val user =
                userRepository.getUserFromFirestore(firebaseAuth.currentUser?.uid ?: "").getOrNull()

            if (user != null) {
                return ResourceState.Success(user)
            }

            if (!isTokenExpired(tokens.idToken)) {
                val credential = when (tokens.provider) {
                    "google.com" -> GoogleAuthProvider.getCredential(tokens.idToken, null)
                    "password" -> EmailAuthProvider.getCredential(tokens.idToken, "s")
                    else -> return ResourceState.Error("Unknown provider")
                }

                val authResult = firebaseAuth.signInWithCredential(credential).await()

                if (authResult?.user != null) {
                    val userAuth =
                        userRepository.getUserFromFirestore(authResult.user?.uid ?: "").getOrNull()
                    return ResourceState.Success(userAuth)
                }
            }

            val credential = when (tokens.provider) {
                "google.com" -> GoogleAuthProvider.getCredential(tokens.refreshToken, null)
                "password" -> EmailAuthProvider.getCredential(tokens.refreshToken, "s")
                else -> return ResourceState.Error("Unknown provider")
            }
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            if (authResult?.user != null) {
                val newIdToken = authResult.user!!.getIdToken(false).await().token

                if (newIdToken != null) {
                    sharedPreferences.saveAuthTokens(tokens.copy(idToken = newIdToken))
                    val userAuth =
                        userRepository.getUserFromFirestore(authResult.user?.uid ?: "").getOrNull()
                    ResourceState.Success(userAuth)
                } else {
                    ResourceState.Error("Failed to get new token")
                }
            } else {
                ResourceState.Error("Failed to sign in with refresh token")
            }

        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException -> {
                    sharedPreferences.clearAuthTokens()
                    ResourceState.Error("Session expired. Please login again")
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    sharedPreferences.clearAuthTokens()
                    ResourceState.Error("Session expired. Please login again")
                }

                else -> ResourceState.Error(e.message)
            }
        }
    }

    override suspend fun signInWithPassword(email: String, password: String): ResourceState<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            if (!checkEmailVerify()) {
                return ResourceState.Error("Email not verified")
            }

            val user = userRepository.getUserFromFirestore(result.user?.uid ?: "").getOrNull()

            if (user == null) {
                return ResourceState.Error("User not found")
            }

            Log.d("signInWithPassword", user.isActive.toString())

            if (!user.isActive) {
                return ResourceState.Error("Account is disabled, please check your email for reset password")
            }

            val refreshToken = result.user!!.getIdToken(false).await().token ?: ""
            val idToken = result.user!!.getIdToken(true).await().token ?: ""
            sharedPreferences.saveAuthTokens(AuthTokens(idToken, refreshToken, "password"))

            ResourceState.Success(user)
        } catch (e: Exception) {
            ResourceState.Error("Password is incorrect")
        }
    }

    override suspend fun checkEmailVerify(): Boolean {
        val user = firebaseAuth.currentUser
        user?.reload()
        return user?.isEmailVerified ?: false
    }

    override suspend fun checkEmailExist(email: String): ResourceState<Boolean> {
        val snapshot = firestore.collection("users").get().await()
        for (document in snapshot.documents) {
            if (email == document.get("email")) {
                return ResourceState.Success(true)
            }
        }
        return ResourceState.Error("Email not exist")
    }

    override suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        phone: String,
        context: Context,
    ): ResourceState<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            if (result.user == null) {
                return ResourceState.Error("Failed to create user")
            }

            val user = mapToUser(result.user!!, "password", UserRole.CUSTOMER.name)

            userRepository.saveUserToFirestore(
                user.copy(
                    name = fullName,
                    email = email,
                    phoneNumber = phone,
                )
            )

            firebaseAuth.currentUser?.sendEmailVerification()?.await()

            ResourceState.Success(user)
        } catch (e: Exception) {
            ResourceState.Error("Failed to create account")
        }
    }

    override suspend fun signOut(): ResourceState<Boolean> {
        return try {
            sharedPreferences.clearAuthTokens()
            firebaseAuth.signOut()
            ResourceState.Success(true)
        } catch (e: Exception) {
            sharedPreferences.clearAuthTokens()
            ResourceState.Error(e.message)
        }
    }

    override suspend fun resetPassword(email: String): ResourceState<Boolean> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()

            ResourceState.Success(true)
        } catch (e: Exception) {
            ResourceState.Error("Failed to send reset password email")
        }
    }
}