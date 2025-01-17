package org.nullgroup.lados.data.repositories.implementations.common

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.nullgroup.lados.data.repositories.interfaces.common.UserPreferencesRepository

class RecentUserSearchPreferencesRepository (
    private val dataStore: DataStore<Preferences>
): UserPreferencesRepository<String> {
    val recentUserSearches: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            it[RECENT_USER_SEARCH] ?: setOf()
        }

    override suspend fun modify(data: String) {
        dataStore.edit { preferences ->
            val recentUserSearches = preferences[RECENT_USER_SEARCH] ?: setOf()
            if (preferences[RECENT_USER_SEARCH]!!.contains(data)) {
                Log.d(TAG, "Data already exists")
                preferences[RECENT_USER_SEARCH] = preferences[RECENT_USER_SEARCH]!!.filter{
                    it != data
                }.toSet()
            }
            preferences[RECENT_USER_SEARCH] = recentUserSearches.plus(data)
            Log.d(TAG, "Preferences: ${preferences[RECENT_USER_SEARCH]}")
        }
    }

    override suspend fun get(): String {
        return recentUserSearches.first().first()
    }

    override suspend fun reset() {
        dataStore.edit { preferences ->
            preferences.remove(RECENT_USER_SEARCH)
        }
    }

    companion object {
        const val TAG = "UserPreferencesRepo"
        private val RECENT_USER_SEARCH = stringSetPreferencesKey("RECENT_USER_SEARCH")
    }
}