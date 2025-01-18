package org.nullgroup.lados.data.repositories.implementations.common

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.nullgroup.lados.data.repositories.interfaces.common.UserPreferencesRepository
import org.nullgroup.lados.utilities.SupportedRegion

class LocaleUserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository<SupportedRegion> {
    val locale: Flow<SupportedRegion> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            val regionString = it[REGION] ?: SupportedRegion.VIETNAM.name
            Log.d(TAG, "locale: $regionString")
            val region = SupportedRegion.entries.firstOrNull { region ->
                region.locale.toLanguageTag() == regionString
            } ?: SupportedRegion.VIETNAM
            Log.d(TAG, "locale: $region")
            region
        }

    override suspend fun modify(data: SupportedRegion) {
        dataStore.edit { preferences ->
            preferences[REGION] = data.locale.toLanguageTag()
        }
    }

    override suspend fun get(): SupportedRegion {
        return locale.first()
    }

    override suspend fun reset() {
        dataStore.edit { preferences ->
            preferences.remove(REGION)
        }
    }

    companion object {
        val TAG = "UserPreferencesRepo"
        private val REGION = stringPreferencesKey("REGION")
    }
}