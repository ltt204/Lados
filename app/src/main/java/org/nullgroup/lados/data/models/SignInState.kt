package org.nullgroup.lados.data.models

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
)