package org.nullgroup.lados.data.repositories.implementations

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.R
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.data.repositories.interfaces.GoogleAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.SharedPreferencesRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import org.nullgroup.lados.viewmodels.common.states.ResourceState
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthRepositoryImpl(
    private val context: Context,
    private val oneTapClient: SignInClient,
    private val auth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferencesRepository,
) : GoogleAuthRepository {

    override suspend fun signIn(): IntentSender? {
        return try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await().pendingIntent.intentSender
        } catch (e: Exception) {
            try {
                PendingIntent.getActivity(
                    context,
                    0,
                    googleSignInClient.signInIntent,
                    PendingIntent.FLAG_IMMUTABLE
                ).intentSender
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                null
            }
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun handleCollision(exception: FirebaseAuthUserCollisionException) {
        val credential = exception.updatedCredential
        if (credential == null) {
            Log.d("GoogleAuthRepositoryImpl", "Email link failed")
            return
        }

        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("GoogleAuthRepositoryImpl", "Email link successful")
                } else {
                    Log.d("GoogleAuthRepositoryImpl", "Email link failed")
                }
            }
    }

    private fun linkWithEmailPassword(email: String) {
        val fireAuth = auth.currentUser?.let { user ->
            val credential = EmailAuthProvider.getCredential(email, "fake_password")
            user.linkWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("GoogleAuthRepositoryImpl", "Email link successful")
                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            handleCollision(task.exception as FirebaseAuthUserCollisionException)
                        } else {
                            Log.d("GoogleAuthRepositoryImpl", "Email link failed")
                        }
                    }
                }
        }
    }

    private fun linkWithIfExists(email: String?) {
        email?.let {
            auth.fetchSignInMethodsForEmail(it)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods != null && signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                            linkWithEmailPassword(it)
                        }
                    } else {
                        Log.d("GoogleAuthRepositoryImpl", "Error fetching sign in methods")
                    }
                }
        }
    }

    override suspend fun signInWithIntent(intent: Intent): ResourceState<User> {
        return try {
            val credential = try {
                oneTapClient.getSignInCredentialFromIntent(intent)
            } catch (e: ApiException) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                val account = task.getResult(ApiException::class.java)
                GoogleAuthProvider.getCredential(account?.idToken, null)
            }

            val firebaseCredential = when (credential) {
                is SignInCredential -> GoogleAuthProvider.getCredential(
                    credential.googleIdToken,
                    null
                )

                is AuthCredential -> credential
                else -> throw IllegalStateException("Invalid credential type")
            }

            val authResult = auth.signInWithCredential(firebaseCredential).await()

            var user: User? = null
            authResult.user?.let {
                val provider =
                    if (it.providerData.size >= 2) it.providerData[1].providerId else it.providerId

                user = User(
                    id = it.uid,
                    name = it.displayName ?: "",
                    email = it.email ?: "",
                    role = UserRole.CUSTOMER.name,
                    phoneNumber = it.phoneNumber ?: "",
                    avatarUri = it.photoUrl?.toString() ?: "",
                    provider = provider,
                )

                val token = it.getIdToken(true).await()?.token
                if (token != null) {
                    sharedPreferences.saveData("token", token)
                }
            }


            if (authResult.additionalUserInfo!!.isNewUser) {
                if (user != null) {
                    linkWithIfExists(authResult.user?.email!!)
                    userRepository.saveUserToFirestore(user!!)
                }
            }

            ResourceState.Success(user)
        } catch (e: Exception) {
            ResourceState.Error(e.message)
        }
    }

    override suspend fun signOut(): ResourceState<Boolean> {
        return try {
            oneTapClient.signOut().await()
            auth.signOut()
            ResourceState.Success(true)
        } catch (e: Exception) {
            ResourceState.Error(e.message)
        }
    }
}