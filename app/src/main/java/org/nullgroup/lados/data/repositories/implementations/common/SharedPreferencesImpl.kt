package org.nullgroup.lados.data.repositories.implementations.common

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.nullgroup.lados.R
import org.nullgroup.lados.data.models.AuthTokens
import org.nullgroup.lados.data.repositories.interfaces.common.SharedPreferencesRepository

class SharedPreferencesImpl(
    private val context: Context,
) : SharedPreferencesRepository {

    private var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = getEncryptedSharedPrefs()
    }

    private fun getEncryptedSharedPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            context.getString(R.string.encrypt_auth_prefs),
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun saveAuthTokens(tokens: AuthTokens) {
        sharedPreferences.edit().apply {
            putString("id_token", tokens.idToken)
            putString("refresh_token", tokens.refreshToken)
            putString("provider", tokens.provider)
        }.apply()
    }

    override fun getAuthTokens(): AuthTokens? {
        val idToken = sharedPreferences.getString("id_token", null)
        val refreshToken = sharedPreferences.getString("refresh_token", null)
        val provider = sharedPreferences.getString("provider", null)

        return if (idToken != null && refreshToken != null && provider != null) {
            AuthTokens(idToken, refreshToken, provider)
        } else null
    }

    override fun clearAuthTokens() {
        sharedPreferences.edit().clear().apply()
    }
}