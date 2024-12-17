package org.nullgroup.lados.data.repositories.implementations

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import org.nullgroup.lados.data.repositories.interfaces.SharedPreferencesRepository

class SharedPreferencesImpl(
    private val context: Context,
) : SharedPreferencesRepository {

    private var sharedPreferences: SharedPreferences

    init {
        sharedPreferences =
            context.getSharedPreferences(encodeString("token"), Context.MODE_PRIVATE)
    }

    private fun encodeString(input: String): String {
        return Base64.encodeToString(input.toByteArray(), Base64.DEFAULT)
    }

    private fun decodeString(input: String): String {
        return String(Base64.decode(input, Base64.DEFAULT))
    }

    override fun saveData(key: String, value: String) {
        val keyEncode = encodeString(key)
        val valueEncode = encodeString(value)
        sharedPreferences.edit().putString(keyEncode, valueEncode).apply()
    }

    override fun getData(key: String): String? {
        val keyEncode = encodeString(key)
        val valueEncode = sharedPreferences.getString(keyEncode, null)
        return valueEncode?.let { decodeString(it) }
    }

    override fun clearData(key: String) {
        val keyEncode = encodeString(key)
        sharedPreferences.edit().remove(keyEncode).apply()
    }
}