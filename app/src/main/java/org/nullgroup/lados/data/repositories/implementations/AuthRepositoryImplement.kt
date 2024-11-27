package org.nullgroup.lados.data.repositories.implementations


import androidx.activity.ComponentActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.Login
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.data.repositories.interfaces.AuthRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository

class AuthRepositoryImplement(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userRepos: UserRepository,
    private val loginManager: LoginManager,
    private val callbackManager: CallbackManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun loginWithFacebook(activity: ComponentActivity): LoginState {
        var loginState: LoginState
        loginManager.logInWithReadPermissions(activity, listOf("email", "public_profile"))
        loginManager.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    loginState = LoginState.Error("Facebook login canceled")
                }

                override fun onError(error: FacebookException) {
                    loginState = LoginState.Error(error.message)
                }

                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                    loginState = LoginState.Success
                }
            })
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null) {
            val credential = FacebookAuthProvider.getCredential(accessToken.token)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                loginState = LoginState.Success
            } else {
                loginState = LoginState.Error("Facebook sign-in failed")
            }
        } else {
            loginState = LoginState.Error("Access token is null")
        }
        return loginState
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken?): LoginState {
        if (accessToken == null) {
            return LoginState.Error("Access token is null")
        }
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        var loginState: LoginState = LoginState.Initial
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginState = LoginState.Success
                } else {
                    loginState =
                        LoginState.Error(task.exception?.message ?: "Authentication failed")
                }
            }

        return loginState
    }

    override suspend fun loginWithTwitter(): Result<Boolean> {
//        return try {
//            val provider = OAuthProvider.newBuilder("twitter.com")
//            val result = auth.startActivityForSignInWithProvider(, provider.build()).await()
//            val user = result.user
//            if (user != null) {
//                Result.success(true)
//            } else {
//                Result.failure(Exception("User is null"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
        TODO("Not yet implemented")
    }

    override suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
    ): Result<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                firestore.collection("users")
                    .document(email)
                    .set(
                        mapOf(
                            "name" to fullName,
                            "email" to email,
                            "role" to UserRole.CUSTOMER.name
                        )
                    ).await()
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Boolean> {
        auth.signOut()
        return Result.success(true)
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Success : LoginState()
    data class Error(val message: String?) : LoginState()
}