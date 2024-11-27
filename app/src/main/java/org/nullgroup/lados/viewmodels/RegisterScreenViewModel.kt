package org.nullgroup.lados.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.RegisterState
import org.nullgroup.lados.data.repositories.interfaces.AuthRepository
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _result = MutableStateFlow<RegisterState>(RegisterState.Initialize)
    val result = _result.asStateFlow()

    fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ) {
        _result.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                authRepository.signUp("$firstName $lastName", email, password).let {
                    if (it.isSuccess) {
                        _result.value = RegisterState.Success
                    } else {
                        _result.value = RegisterState.Error(it.exceptionOrNull()?.message)
                    }
                }
            } catch (e: Exception) {
                _result.value = RegisterState.Error(e.message)
            }
        }
    }
}