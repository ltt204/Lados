package org.nullgroup.lados.viewmodels

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.repositories.implementations.LoginState
import org.nullgroup.lados.data.repositories.interfaces.AuthRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    var result = MutableStateFlow<Result<Boolean>>(Result.success(false))
        private set
    var userRole = MutableStateFlow<String?>(null)
        private set
    var email = MutableStateFlow<String?>(null)
        private set
    var loginStep = MutableStateFlow<LoginStep>(LoginStep.Email)
        private set
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState = _loginState.asStateFlow()

    fun onBackPressed() {
        if (loginStep.value is LoginStep.Password) {
            loginStep.value = LoginStep.Email
        }
    }

    fun setEmail(email: String) {
        this.email.value = email
    }

    fun login(password: String) {
        viewModelScope.launch {
            try {
                authRepository.login(email.value!!, password).let {
                    result.value = it
                    Log.d("LoginScreenViewModel", "login: ${result.value}")
                }

                if (result.value.isSuccess) {
                    userRepository.getUserRole(email.value!!).let {
                        userRole.value = it.getOrNull()
                    }
                }

            } catch (e: Exception) {
                result.value = Result.failure(e)
            }
        }
    }

    fun validateEmail() {
        viewModelScope.launch {
            if (isValidateEmail(email.value!!)) {
                loginStep.value = LoginStep.Password
            }
        }
    }

    private fun isValidateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun loginWithFacebook(activity: ComponentActivity) {
        viewModelScope.launch {
            _loginState.value = authRepository.loginWithFacebook(activity)
        }
    }
}

sealed class LoginStep {
    object Email : LoginStep()
    object Password : LoginStep()
}