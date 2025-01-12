package org.nullgroup.lados.viewmodels.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.repositories.implementations.common.LocaleUserPreferencesRepository
import org.nullgroup.lados.data.repositories.implementations.common.ThemeUserPreferencesRepository
import org.nullgroup.lados.utilities.SupportedRegion
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val themeUserPreferencesRepository: ThemeUserPreferencesRepository,
    private val localeUserPreferencesRepository: LocaleUserPreferencesRepository
) : ViewModel() {
    val darkMode = themeUserPreferencesRepository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val locale = localeUserPreferencesRepository.locale
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SupportedRegion.entries.first()
        )

    fun modifyTheme() {
        viewModelScope.launch {
            themeUserPreferencesRepository.modify(darkMode.value.not())
        }
    }

    fun saveLocale(locale: Locale) {
        viewModelScope.launch {
            Log.d("SettingViewModel", "saveLocale: $locale")
            localeUserPreferencesRepository.modify(
                SupportedRegion.entries.firstOrNull { it.locale == locale }
                    ?: SupportedRegion.VIETNAM
            )
        }
    }
}