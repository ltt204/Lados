package org.nullgroup.lados.viewmodels.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.repositories.UserPreferencesRepository
import org.nullgroup.lados.utilities.SupportedRegion
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val darkMode = userPreferencesRepository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val locale = userPreferencesRepository.locale
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SupportedRegion.US
        )

    fun modifyTheme() {
        viewModelScope.launch {
            userPreferencesRepository.modifyTheme(darkMode.value.not())
        }
    }

    fun saveLocale(locale: Locale) {
        viewModelScope.launch {
            Log.d("SettingViewModel", "saveLocale: $locale")
            userPreferencesRepository.saveLocale(locale)
        }
    }

    suspend fun reset() {
        userPreferencesRepository.reset()
    }
}