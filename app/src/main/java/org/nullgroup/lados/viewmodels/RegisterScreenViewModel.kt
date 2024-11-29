package org.nullgroup.lados.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.repositories.interfaces.EmailAuthRepository
import org.nullgroup.lados.viewmodels.events.RegisterScreenEvent
import org.nullgroup.lados.viewmodels.states.RegisterScreenState
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val emailAuth: EmailAuthRepository
) : ViewModel() {
    var registerState = MutableStateFlow<RegisterScreenState>(RegisterScreenState.Idle)
        private set

    private fun handleSignUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ) {
        registerState.value = RegisterScreenState.Loading
        viewModelScope.launch {
            try {
                emailAuth.signUp("$firstName $lastName", email, password).let {
                    if (it.isSuccess) {
                        registerState.value = RegisterScreenState.Success
                    } else {
                        registerState.value =
                            RegisterScreenState.Error(it.exceptionOrNull()?.message)
                    }
                }
            } catch (e: Exception) {
                registerState.value = RegisterScreenState.Error(e.message)
            }
        }
    }

    fun handleEvent(event: RegisterScreenEvent) {
        when (event) {
            is RegisterScreenEvent.HandleSignUp -> {
                handleSignUp(event.firstName, event.lastName, event.email, event.password)
            }

            RegisterScreenEvent.HandleBackStack -> TODO()
        }
    }
}