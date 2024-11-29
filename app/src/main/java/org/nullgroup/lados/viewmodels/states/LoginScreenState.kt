package org.nullgroup.lados.viewmodels.states

import org.nullgroup.lados.data.models.UserRole

sealed class LoginScreenState {
    data object Idle: LoginScreenState()
    data object Loading: LoginScreenState()
    data class Error(val message: String?): LoginScreenState()
    data class Success(val userRole: String?): LoginScreenState()
}