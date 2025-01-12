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
import org.nullgroup.lados.data.repositories.implementations.common.LocaleUserPreferencesRepository
import org.nullgroup.lados.data.repositories.implementations.common.RecentUserSearchPreferencesRepository
import org.nullgroup.lados.data.repositories.implementations.common.ThemeUserPreferencesRepository
import javax.inject.Singleton

private const val USER_PREFERENCES = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    USER_PREFERENCES
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
    fun provideThemeUserPreferencesRepository(dataStore: DataStore<Preferences>) =
        ThemeUserPreferencesRepository(dataStore)

    @Singleton
    @Provides
    fun provideLocaleUserPreferencesRepository(dataStore: DataStore<Preferences>) =
        LocaleUserPreferencesRepository(dataStore)

    @Singleton
    @Provides
    fun provideRecentUserSearchPreferencesRepository(dataStore: DataStore<Preferences>) =
        RecentUserSearchPreferencesRepository(dataStore)
}