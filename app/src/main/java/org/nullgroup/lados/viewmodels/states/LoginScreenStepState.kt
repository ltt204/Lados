package org.nullgroup.lados.viewmodels.states

sealed class LoginScreenStepState {
    data class Email(var email: String? = null) : LoginScreenStepState()
    data class Password(var email: String, var password: String? = null) : LoginScreenStepState()
}