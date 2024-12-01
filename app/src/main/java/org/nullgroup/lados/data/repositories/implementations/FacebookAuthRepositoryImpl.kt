package org.nullgroup.lados.data.repositories.implementations

import androidx.activity.ComponentActivity
import androidx.core.content.res.ResourcesCompat
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.data.repositories.interfaces.FacebookAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import org.nullgroup.lados.viewmodels.states.ResourceState

class FacebookAuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val loginManager: LoginManager,
    private val callbackManager: CallbackManager,
    private val userRepository: UserRepository,
) : FacebookAuthRepository {

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun signIn(activity: ComponentActivity): ResourceState<User> {
        var resultLogin: ResourceState<User> = ResourceState.Idle
        loginManager.logInWithReadPermissions(activity, listOf("email", "public_profile"))
        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    resultLogin = ResourceState.Error("Facebook login canceled")
                }

                override fun onError(error: FacebookException) {
                    resultLogin = ResourceState.Error(error.message)
                }

                override fun onSuccess(result: LoginResult) {
                    GlobalScope.launch {
                        resultLogin = handleFacebookAccessToken(result.accessToken)
                    }
                }
            }
        )


        return resultLogin
    }

    private suspend fun handleFacebookAccessToken(accessToken: AccessToken?): ResourceState<User> {
        if (accessToken == null) {
            return ResourceState.Error("Token is null")
        }

        val resultLogin: ResourceState<User>

        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        val authResult = auth.signInWithCredential(credential).await()

        val user = authResult.user?.let {
            User(
                name = it.displayName ?: "",
                email = it.email ?: "",
                photoUrl = it.photoUrl.toString(),
                provider = it.providerId ?: "",
                token = it.getIdToken(true).await()?.token ?: "",
                role = UserRole.CUSTOMER.name,
                phoneNumber = it.phoneNumber ?: "",
            )
        }

        if (user != null) {
            if (authResult.additionalUserInfo!!.isNewUser) {
                userRepository.addUserToFirestore(user)
            }
            resultLogin = ResourceState.Success(user)
        } else {
            resultLogin = ResourceState.Error("User not found")
        }

        return resultLogin
    }

    override suspend fun signOut(): ResourceState<Boolean> {
        return try {
            auth.signOut()
            ResourceState.Success(true)
        } catch (e: Exception) {
            ResourceState.Error(e.message)
        }
    }
}