package org.nullgroup.lados.data.models

sealed class ForgotPasswordState() {
    object Initialize : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String?) : ForgotPasswordState()
}
