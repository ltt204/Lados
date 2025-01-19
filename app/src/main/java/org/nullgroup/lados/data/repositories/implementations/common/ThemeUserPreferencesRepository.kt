package org.nullgroup.lados.data.repositories.implementations.common

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.nullgroup.lados.data.repositories.interfaces.common.UserPreferencesRepository

class ThemeUserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository<Boolean> {
    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            it[DARK_MODE] ?: false
        }


    override suspend fun modify(data: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = data
        }
    }

    companion object {
        private val DARK_MODE = booleanPreferencesKey("DARK_MODE")
        private const val TAG = "UserPreferencesRepo"
    }

    override suspend fun get(): Boolean {
        return isDarkMode.first()
    }

    override suspend fun reset() {
        dataStore.edit { preferences ->
            preferences.remove(DARK_MODE)
        }
    }
}