package org.nullgroup.lados.viewmodels.common

import android.content.Context
import android.util.Patterns.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.AuthRepository
import org.nullgroup.lados.viewmodels.common.states.LoginScreenStepState
import org.nullgroup.lados.viewmodels.common.states.ResourceState
import org.nullgroup.lados.viewmodels.common.events.LoginScreenEvent
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val auth: AuthRepository,
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

            viewModelScope.launch {

                when (val state = auth.checkEmailExist(email)) {
                    is ResourceState.Error -> {
                        loginState.value = ResourceState.Error(state.message)
                    }

                    ResourceState.Idle -> {

                    }

                    ResourceState.Loading -> {

                    }

                    is ResourceState.Success -> {
                        loginStep.value = LoginScreenStepState.Password(email)
                        loginState.value = ResourceState.Idle
                    }
                }
            }
        } else {
            loginState.value = ResourceState.Error("Invalid email")
        }
    }

    private fun handleEnterPassword(password: String) {
        try {
            val email = (loginStep.value as LoginScreenStepState.Password).email

            viewModelScope.launch {

                auth.signInWithPassword(email, password).let { result ->
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

    private fun handleLogInWithGoogle(context: Context) {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                loginState.value = ResourceState.Error("Please update Google Play Services")
                return
            } else {
                loginState.value =
                    ResourceState.Error("Devices that do not support Google Play Services")
                return
            }
        }

        viewModelScope.launch {
            loginState.value = auth.signInWithGoogle(context)

            when (val state = loginState.value) {
                is ResourceState.Error -> {}
                ResourceState.Idle -> {}
                ResourceState.Loading -> {}
                is ResourceState.Success -> {
                    loginStep.value = LoginScreenStepState.Home(state.data!!)
                }
            }
        }
    }

    private fun handleAutoSignIn() = viewModelScope.launch {
        loginState.value = auth.autoSignIn()

        when (val state = loginState.value) {
            is ResourceState.Error -> {}
            ResourceState.Idle -> {}
            ResourceState.Loading -> {}
            is ResourceState.Success -> {
                loginStep.value = LoginScreenStepState.Home(state.data!!)
            }
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

            is LoginScreenEvent.HandleLogInWithGoogle -> {
                handleLogInWithGoogle(event.context)
            }

            is LoginScreenEvent.HandleSignUp -> {
                handleSignUp(event.navController)
            }

            is LoginScreenEvent.HandleAutoSignIn -> {
                handleAutoSignIn()
            }
        }
    }
}