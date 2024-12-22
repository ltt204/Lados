package org.nullgroup.lados.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

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

    private companion object {
        val DARK_MODE = booleanPreferencesKey("DARK_MODE")
        const val TAG = "UserPreferencesRepo"
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