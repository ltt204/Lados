package org.nullgroup.lados.viewmodels.states

import org.nullgroup.lados.data.models.User

sealed class LoginScreenStepState {
    data class Email(var email: String? = null) : LoginScreenStepState()
    data class Password(var email: String, var password: String? = null) : LoginScreenStepState()
    data class Home(val user: User) : LoginScreenStepState()
}