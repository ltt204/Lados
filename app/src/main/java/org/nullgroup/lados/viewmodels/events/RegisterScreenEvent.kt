package org.nullgroup.lados.viewmodels.events

import androidx.navigation.NavController

sealed class RegisterScreenEvent {
    data class HandleSignUp(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String,
    ) : RegisterScreenEvent()
    data class HandleLogin(
        val email: String,
        val password: String,
    ) : RegisterScreenEvent()
}