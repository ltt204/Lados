package org.nullgroup.lados.viewmodels.states

sealed class ForgotPasswordScreenState {
    object Idle : ForgotPasswordScreenState()
    object Loading : ForgotPasswordScreenState()
    object Success : ForgotPasswordScreenState()
    data class Error(val message: String?) : ForgotPasswordScreenState()
}
