package org.nullgroup.lados.data.repositories.implementations

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.data.repositories.interfaces.EmailAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.SharedPreferencesRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import org.nullgroup.lados.viewmodels.common.states.ResourceState
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class EmailAuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferencesRepository,
) : EmailAuthRepository {

    override suspend fun signIn(email: String, password: String): ResourceState<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()

            if (!checkEmailVerify()) {
                return ResourceState.Error("Email not verified")
            }

            val user = userRepository.getUserFromFirestore(result.user?.uid ?: "").getOrNull()

            if (user == null) {
                return ResourceState.Error("User not found")
            }

            if (!user.isActive) {
                return ResourceState.Error("Account is disabled, please check your email for reset password")
            }

            result.user?.getIdToken(true)?.addOnSuccessListener {
                sharedPreferences.saveData("token", it?.token!!)
            }

            ResourceState.Success(user)
        } catch (e: Exception) {
            ResourceState.Error("Password is incorrect")
        }
    }

    override suspend fun checkEmailVerify(): Boolean {
        val user = auth.currentUser
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
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            if (result.user == null) {
                return ResourceState.Error("Failed to create user")
            }

            val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)

            if (googleSignInAccount != null) {
                val credential = GoogleAuthProvider.getCredential(
                    googleSignInAccount.idToken,
                    null
                )

                val linkResult = suspendCoroutine<Boolean> { continuation ->
                    result.user?.linkWithCredential(credential)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("SignUp", "Credential linked successfully")
                                continuation.resume(true)
                            } else {
                                Log.e("SignUp", "Credential linking failed", task.exception)
                                continuation.resume(false)
                            }
                        }
                }

                if (!linkResult) {
                    return ResourceState.Error("Failed to link Google account")
                }
            }

            val user = User(
                id = result.user?.uid ?: "",
                email = email,
                name = fullName,
                role = UserRole.CUSTOMER.name,
                phoneNumber = phone,
                avatarUri = googleSignInAccount?.photoUrl?.toString() ?: "",
                provider = "email_google",
                isActive = true,
            )

            // Save token
            val token = result.user?.getIdToken(true)?.await()?.token
            if (token != null) {
                sharedPreferences.saveData("token", token)
            }

            userRepository.saveUserToFirestore(user)

            auth.currentUser?.sendEmailVerification()?.await()

            ResourceState.Success(user)
        } catch (e: Exception) {
            ResourceState.Error("Failed to create account")
        }
    }

    override suspend fun signOut(): ResourceState<Boolean> {
        return try {
            auth.signOut()
            ResourceState.Success(true)
        } catch (e: Exception) {
            ResourceState.Error(e.message)
        }
    }

    override suspend fun resetPassword(email: String): ResourceState<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()

            ResourceState.Success(true)
        } catch (e: Exception) {
            ResourceState.Error("Failed to send reset password email")
        }
    }
}