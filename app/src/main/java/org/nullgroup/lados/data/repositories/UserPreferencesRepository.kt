package org.nullgroup.lados.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okhttp3.internal.wait
import org.nullgroup.lados.utilities.SupportedRegion
import java.util.Locale

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            it[DARK_MODE] ?: false
        }

    val locale: Flow<SupportedRegion> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            Log.d(TAG, "locale: ${it[REGION]}")
            val regionString = it[REGION] ?: SupportedRegion.VIETNAM.name
            Log.d(TAG, "locale: $regionString")
            val region = SupportedRegion.entries.firstOrNull {
                Log.d(TAG, "locale: ${it.locale.toLanguageTag()}")
                it.locale.toLanguageTag() == regionString
            } ?: SupportedRegion.VIETNAM
            Log.d(TAG, "locale: $region")
            region
        }

    suspend fun saveLocale(locale: Locale) {
        dataStore.edit { preferences ->
            Log.d(TAG, "saveLocale: $locale")
            preferences[REGION] = locale.toLanguageTag()
            Log.d(TAG, "saveLocale: ${preferences[REGION]}")
            Locale.setDefault(locale)
        }
    }

    private companion object {
        const val TAG = "UserPreferencesRepo"

        val DARK_MODE = booleanPreferencesKey("DARK_MODE")
        val REGION = stringPreferencesKey("REGION")
    }

    suspend fun modifyTheme(isDarkMode: Boolean) {
        dataStore.edit { preference ->
            preference[DARK_MODE] = isDarkMode
        }
    }

    suspend fun reset() {
        dataStore.edit {
            it.clear()
        }
    }
}