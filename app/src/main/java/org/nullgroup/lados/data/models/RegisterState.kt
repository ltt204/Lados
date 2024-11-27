package org.nullgroup.lados.data.models

sealed class RegisterState() {
    data object Initialize : RegisterState()
    data object Loading : RegisterState()
    data object Success : RegisterState()
    data class Error(val message: String?) : RegisterState()
}