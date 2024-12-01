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
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.data.repositories.interfaces.EmailAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.FacebookAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.GoogleAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import org.nullgroup.lados.viewmodels.events.LoginScreenEvent
import org.nullgroup.lados.viewmodels.states.LoginScreenState
import org.nullgroup.lados.viewmodels.states.LoginScreenStepState
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val emailAuth: EmailAuthRepository,
    private val googleAuth: GoogleAuthRepository,
    private val facebookAuth: FacebookAuthRepository
) : ViewModel() {

    var loginStep = MutableStateFlow<LoginScreenStepState>(LoginScreenStepState.Email())
        private set
    var loginState = MutableStateFlow<LoginScreenState>(LoginScreenState.Idle)
        private set

    fun onBackPressed() {
        when (val currentStep = loginStep.value) {
            is LoginScreenStepState.Email -> {}

            is LoginScreenStepState.Password -> {
                val email = currentStep.email
                loginStep.value = LoginScreenStepState.Email(email)
            }
        }
    }

    fun isValidateEmail(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun handleEnterEmail(email: String) {
        if (isValidateEmail(email ?: "")) {
            loginStep.value = LoginScreenStepState.Password(email)
            loginState.value = LoginScreenState.Idle
        } else {
            loginState.value = LoginScreenState.Error("Invalid email")
        }

        Log.d("LoginScreenViewModel", "handleEnterEmail: $email")
    }

    private fun handleEnterPassword(password: String) {
        var currentState = loginState.value
        val email = (loginStep.value as LoginScreenStepState.Password).email
        Log.d("test", "$email $password")

        viewModelScope.launch {
            emailAuth.signIn(email, password).let { result ->
                if (result.isSuccess) {
                    currentState = LoginScreenState.Success(UserRole.CUSTOMER.name)
                } else if (result.isFailure) {
                    currentState =
                        LoginScreenState.Error(result.exceptionOrNull()?.message)
                    Log.d(
                        "LoginScreenViewModel",
                        "handleEnterPassword: ${result.exceptionOrNull()?.message}"
                    )
                }
            }

            when (currentState) {
                is LoginScreenState.Error -> {
                    loginState.value = currentState
                }

                LoginScreenState.Idle -> {
                    loginState.value = currentState
                }

                LoginScreenState.Loading -> {
                    loginState.value = currentState
                }

                is LoginScreenState.Success -> {
                    userRepository.getUserRole(email).let { userRole ->
                        loginState.value =
                            LoginScreenState.Success(userRole.getOrNull())
                    }
                }
            }
        }
    }

    private fun handleLogInWithFacebook(activity: ComponentActivity) {
        viewModelScope.launch {
            val result = facebookAuth.signIn(activity)
            if (result.isSuccess) {
                loginState.value = LoginScreenState.Success(UserRole.CUSTOMER.name)
            } else if (result.isFailure) {
                loginState.value = LoginScreenState.Error(result.exceptionOrNull()?.message)
                Log.d(
                    "LoginScreenViewModel",
                    "handleLogInWithFacebook: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    private fun handleLogInWithGoogle(launcher: ActivityResultLauncher<IntentSenderRequest>) {
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
        loginState.value = LoginScreenState.Idle
    }

    private fun handleForgotPassword(navController: NavController) {
        navController.navigate("forgot_password")
        loginState.value = LoginScreenState.Idle
    }

    fun onGoogleSignInResult(result: ActivityResult) {
        viewModelScope.launch {
            val signInResult = googleAuth.signInWithIntent(
                intent = result.data ?: return@launch
            )
        }
    }

    fun handleLoginEvent(event: LoginScreenEvent) {
        loginState.value = LoginScreenState.Loading
        when (event) {
            is LoginScreenEvent.HandleEnterEmail -> {
                handleEnterEmail(event.email)
            }

            is LoginScreenEvent.HandleEnterPassword -> {
                try {
                    handleEnterPassword(event.password)
                } catch (e: Exception) {
                    loginState.value = LoginScreenState.Error(e.message)
                    Log.d("LoginScreenViewModel", "handleEnterPassword: ${e.message}")
                }
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