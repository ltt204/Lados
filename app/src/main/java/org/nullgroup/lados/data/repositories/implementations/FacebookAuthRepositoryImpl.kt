package org.nullgroup.lados.data.repositories.implementations

import androidx.activity.ComponentActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.repositories.interfaces.FacebookAuthRepository

class FacebookAuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val loginManager: LoginManager,
    private val callbackManager: CallbackManager
) : FacebookAuthRepository {

    override suspend fun signIn(activity: ComponentActivity): Result<Boolean> {
        var resultLogin = Result.success(false)
        loginManager.logInWithReadPermissions(activity, listOf("email", "public_profile"))
        loginManager.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    resultLogin = Result.failure(Exception("Facebook login canceled"))
                }

                override fun onError(e: FacebookException) {
                    resultLogin = Result.failure(e)
                }

                override fun onSuccess(result: LoginResult) {
                    resultLogin = handleFacebookAccessToken(result.accessToken)
                }
            })

        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null) {
            val credential = FacebookAuthProvider.getCredential(accessToken.token)
            val authResult = auth.signInWithCredential(credential).await()

            val user = authResult.user
            if (user != null) {
                resultLogin = Result.success(true)
            } else {
                resultLogin = Result.failure(Exception("Authentication failed"))
            }
        } else {
            resultLogin = Result.failure(Exception("Access token is null"))
        }

        return resultLogin
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken?): Result<Boolean> {
        if (accessToken == null) {
            return Result.failure(Exception("Access token is null"))
        }

        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        var result = Result.success(false)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result = Result.success(true)
                } else {
                    result = Result.failure(
                        Exception(
                            task.exception?.message ?: "Authentication failed"
                        )
                    )
                }
            }

        return result
    }

    override suspend fun signOut(): Result<Boolean> {
        TODO("Not yet implemented")
    }
}