package org.nullgroup.lados.viewmodels.states

sealed class RegisterScreenState {
    data object Idle: RegisterScreenState()
    data object Loading: RegisterScreenState()
    data object Success: RegisterScreenState()
    data class Error(val message: String?): RegisterScreenState()
}