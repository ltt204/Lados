package org.nullgroup.lados.viewmodels.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.repositories.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val darkMode = userPreferencesRepository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun modifyTheme() {
        viewModelScope.launch {
            userPreferencesRepository.modifyTheme(darkMode.value.not())
        }
    }

    suspend fun reset() {
        userPreferencesRepository.reset()
    }
}