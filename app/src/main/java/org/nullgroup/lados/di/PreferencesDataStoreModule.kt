package org.nullgroup.lados.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.nullgroup.lados.data.repositories.UserPreferencesRepository
import javax.inject.Singleton

private const val DARK_MODE_PREFERENCE_NAME = "dark_mode_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    DARK_MODE_PREFERENCE_NAME
)

@Module
@InstallIn(SingletonComponent::class)
object PreferencesDataStoreModule {
    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Singleton
    @Provides
    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>) =
        UserPreferencesRepository(dataStore)
}