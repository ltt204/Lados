package org.nullgroup.lados.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchHistoryManager(private val context: Context) {

    companion object {
        private const val DATASTORE_NAME = "search_history"
        private val SEARCH_HISTORY_KEY = stringSetPreferencesKey("search_history")
        private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)
    }

    val searchHistory: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SEARCH_HISTORY_KEY] ?: emptySet()
    }

    suspend fun addSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            val currentHistory = preferences[SEARCH_HISTORY_KEY] ?: emptySet()
            preferences[SEARCH_HISTORY_KEY] = currentHistory.toMutableSet().apply {
                add(query)
            }
        }
    }

    suspend fun clearSearchHistory() {
        context.dataStore.edit { preferences ->
            preferences[SEARCH_HISTORY_KEY] = emptySet()
        }
    }

    suspend fun deleteSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            val currentHistory = preferences[SEARCH_HISTORY_KEY] ?: emptySet()
            preferences[SEARCH_HISTORY_KEY] = currentHistory.toMutableSet().apply {
                remove(query) // Remove the specific query
            }
        }
    }
}