package org.nullgroup.lados.viewmodels.common

import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.repositories.interfaces.auth.AuthRepository
import org.nullgroup.lados.viewmodels.common.states.ResourceState
import org.nullgroup.lados.viewmodels.common.events.ForgotPasswordScreenEvent
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordScreenViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {
    var forgotPasswordState =
        MutableStateFlow<ResourceState<Boolean>>(ResourceState.Idle)
        private set

    fun isValidateEmail(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun handleResetPassword(email: String) {
        viewModelScope.launch {
            try {
                auth.resetPassword(email).let {
                    forgotPasswordState.value = it
                }
            } catch (e: Exception) {
                forgotPasswordState.value = ResourceState.Error(e.message)
            }
        }
    }

    private fun handleReturnToLogin(navController: NavController) {
        navController.popBackStack("login", false)
        forgotPasswordState.value = ResourceState.Idle
    }

    fun handleEvent(event: ForgotPasswordScreenEvent) {
        forgotPasswordState.value = ResourceState.Loading
        when (event) {
            is ForgotPasswordScreenEvent.HandleResetPassword -> {
                handleResetPassword(event.email)
            }

            is ForgotPasswordScreenEvent.HandleReturnToLogin -> {
                handleReturnToLogin(event.navController)
            }
        }
    }
}