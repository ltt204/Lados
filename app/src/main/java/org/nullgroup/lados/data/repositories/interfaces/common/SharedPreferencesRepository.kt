package org.nullgroup.lados.data.repositories.interfaces.common

import org.nullgroup.lados.data.models.AuthTokens

interface SharedPreferencesRepository {
    fun saveAuthTokens(tokens: AuthTokens)
    fun getAuthTokens(): AuthTokens?
    fun clearAuthTokens()
}