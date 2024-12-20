package org.nullgroup.lados.data.models

data class AuthTokens(
    val idToken: String,
    val refreshToken: String,
    val provider: String,
)