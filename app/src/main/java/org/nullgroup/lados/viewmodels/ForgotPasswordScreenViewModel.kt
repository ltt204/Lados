package org.nullgroup.lados.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.viewmodels.states.ForgotPasswordScreenState
import org.nullgroup.lados.data.repositories.interfaces.EmailAuthRepository
import org.nullgroup.lados.viewmodels.events.ForgotPasswordScreenEvent
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordScreenViewModel @Inject constructor(
    private val emailAuth: EmailAuthRepository
) : ViewModel() {
    var forgotPasswordState =
        MutableStateFlow<ForgotPasswordScreenState>(ForgotPasswordScreenState.Idle)
        private set

    private fun handleResetPassword(email: String) {
        forgotPasswordState.value = ForgotPasswordScreenState.Loading
        viewModelScope.launch {
            try {
                emailAuth.resetPassword(email).let {
                    if (it.isSuccess) {
                        forgotPasswordState.value = ForgotPasswordScreenState.Success
                    } else {
                        forgotPasswordState.value =
                            ForgotPasswordScreenState.Error(it.exceptionOrNull()?.message)
                    }
                }
            } catch (e: Exception) {
                forgotPasswordState.value = ForgotPasswordScreenState.Error(e.message)
            }
        }
    }

    private fun handleBackStack() {

    }

    private fun handleReturnToLogin(navController: NavController) {
        navController.navigate("login")
    }

    fun handleEvent(event: ForgotPasswordScreenEvent) {
        when (event) {
            is ForgotPasswordScreenEvent.HandleResetPassword -> {
                forgotPasswordState.value = ForgotPasswordScreenState.Loading
                handleResetPassword(event.email)
                forgotPasswordState.value = ForgotPasswordScreenState.Success
            }

            ForgotPasswordScreenEvent.HandleBackStack -> {

            }

            is ForgotPasswordScreenEvent.HandleReturnToLogin -> {
                handleReturnToLogin(event.navController)
            }
        }
    }
}