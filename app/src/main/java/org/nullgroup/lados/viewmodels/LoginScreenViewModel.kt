package org.nullgroup.lados.viewmodels

import android.util.Log
import android.util.Patterns.*
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.data.repositories.interfaces.EmailAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.FacebookAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.GoogleAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import org.nullgroup.lados.viewmodels.events.LoginScreenEvent
import org.nullgroup.lados.viewmodels.states.LoginScreenState
import org.nullgroup.lados.viewmodels.states.LoginScreenStepState
import org.nullgroup.lados.viewmodels.states.ResourceState
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val emailAuth: EmailAuthRepository,
    private val googleAuth: GoogleAuthRepository,
    private val facebookAuth: FacebookAuthRepository,
) : ViewModel() {

    var loginStep = MutableStateFlow<LoginScreenStepState>(LoginScreenStepState.Email())
        private set
    var loginState = MutableStateFlow<ResourceState<User>>(ResourceState.Idle)
        private set

    fun onBackPressed() {
        when (val currentStep = loginStep.value) {
            is LoginScreenStepState.Email -> {}

            is LoginScreenStepState.Password -> {
                val email = currentStep.email
                loginStep.value = LoginScreenStepState.Email(email)
            }

            is LoginScreenStepState.Home -> TODO()
        }
    }

    fun isValidateEmail(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun handleEnterEmail(email: String) {
        if (isValidateEmail(email ?: "")) {
            loginStep.value = LoginScreenStepState.Password(email)
            loginState.value = ResourceState.Idle
        } else {
            loginState.value = ResourceState.Error("Invalid email")
        }

        Log.d("LoginScreenViewModel", "handleEnterEmail: $email")
    }

    private fun handleEnterPassword(password: String) {
        try {
            val email = (loginStep.value as LoginScreenStepState.Password).email

            viewModelScope.launch {
                emailAuth.signIn(email, password).let { result ->
                    loginState.value = result

                    when (result) {
                        is ResourceState.Error -> {
                            loginState.value =
                                ResourceState.Error(result.message)
                        }

                        ResourceState.Idle -> {}
                        ResourceState.Loading -> {}
                        is ResourceState.Success -> {
                            loginStep.value =
                                LoginScreenStepState.Home(result.data!!)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            loginState.value = ResourceState.Error(e.message)
        }
    }

    private fun handleLogInWithFacebook(activity: ComponentActivity) {
        try {
            viewModelScope.launch {
                when (val result = facebookAuth.signIn(activity)) {
                    is ResourceState.Error -> {
                        loginState.value = ResourceState.Error(result.message)
                    }

                    ResourceState.Idle -> TODO()
                    ResourceState.Loading -> TODO()
                    is ResourceState.Success -> {
                        loginState.value = ResourceState.Success(result.data)
                        loginStep.value = LoginScreenStepState.Home(result.data!!)
                    }
                }
            }
        } catch (e: Exception) {
            ResourceState.Error(e.message)
        }

    }

    private fun handleLogInWithGoogle(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
    ) {
        viewModelScope.launch {
            val signInIntentSender = googleAuth.signIn()
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )
        }
    }

    private fun handleSignUp(navController: NavController) {
        navController.navigate("register")
        loginState.value = ResourceState.Idle
    }

    private fun handleForgotPassword(navController: NavController) {
        navController.navigate("forgot_password")
        loginState.value = ResourceState.Idle
    }

    fun onGoogleSignInResult(result: ActivityResult) {

        viewModelScope.launch {
            loginState.value = googleAuth.signInWithIntent(
                intent = result.data ?: return@launch
            )

            when (val state = loginState.value) {
                is ResourceState.Error -> {}
                ResourceState.Idle -> {}
                ResourceState.Loading -> {}
                is ResourceState.Success -> {
                    if (state.data != null) {
                        loginStep.value = LoginScreenStepState.Home(state.data)
                    }
                }
            }
        }
    }

    fun handleLoginEvent(event: LoginScreenEvent) {
        loginState.value = ResourceState.Loading

        when (event) {
            is LoginScreenEvent.HandleEnterEmail -> {
                handleEnterEmail(event.email)
            }

            is LoginScreenEvent.HandleEnterPassword -> {
                handleEnterPassword(event.password)
            }

            is LoginScreenEvent.HandleForgotPassword -> {
                handleForgotPassword(event.navController)
            }

            is LoginScreenEvent.HandleLogInWithFacebook -> {
                handleLogInWithFacebook(event.activity)
            }

            is LoginScreenEvent.HandleLogInWithGoogle -> {
                handleLogInWithGoogle(event.launcher)
            }

            is LoginScreenEvent.HandleSignUp -> {
                handleSignUp(event.navController)
            }
        }
    }
}