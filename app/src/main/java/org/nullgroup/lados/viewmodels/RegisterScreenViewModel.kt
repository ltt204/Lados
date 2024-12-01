package org.nullgroup.lados.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.EmailAuthRepository
import org.nullgroup.lados.viewmodels.events.RegisterScreenEvent
import org.nullgroup.lados.viewmodels.states.RegisterScreenState
import org.nullgroup.lados.viewmodels.states.ResourceState
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val emailAuth: EmailAuthRepository
) : ViewModel() {
    var registerState = MutableStateFlow<ResourceState<User>>(ResourceState.Idle)
        private set

    private fun handleSignUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            try {
                emailAuth.signUp("$firstName $lastName", email, password).let {
                    when (it) {
                        is ResourceState.Error -> {
                            registerState.value = ResourceState.Error(it.message)
                        }

                        ResourceState.Idle -> {}
                        ResourceState.Loading -> {}
                        is ResourceState.Success -> {
                            registerState.value = ResourceState.Success(it.data)
                        }
                    }
                }
            } catch (e: Exception) {
                registerState.value = ResourceState.Error(e.message)
            }
        }
    }

    fun handleEvent(event: RegisterScreenEvent) {
        registerState.value = ResourceState.Loading

        when (event) {
            is RegisterScreenEvent.HandleSignUp -> {
                handleSignUp(event.firstName, event.lastName, event.email, event.password)
            }

            RegisterScreenEvent.HandleBackStack -> {}
        }
    }
}