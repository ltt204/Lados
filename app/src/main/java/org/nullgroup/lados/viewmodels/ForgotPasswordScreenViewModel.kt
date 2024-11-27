package org.nullgroup.lados.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.ForgotPasswordState
import org.nullgroup.lados.data.repositories.interfaces.AuthRepository
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _result = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Initialize)
    val result: StateFlow<ForgotPasswordState> = _result.asStateFlow()

    fun resetPassword(email: String) {
        _result.value = ForgotPasswordState.Loading
        viewModelScope.launch {
            try {
                authRepository.resetPassword(email).let {
                    if (it.isSuccess) {
                        _result.value = ForgotPasswordState.Success
                    } else {
                        _result.value = ForgotPasswordState.Error(it.exceptionOrNull()?.message)
                    }
                }
            } catch (e: Exception) {
                _result.value = ForgotPasswordState.Error(e.message)
            }
        }
    }
}