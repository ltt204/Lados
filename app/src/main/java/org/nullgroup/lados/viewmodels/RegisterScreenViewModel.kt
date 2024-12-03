package org.nullgroup.lados.viewmodels

import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.EmailAuthRepository
import org.nullgroup.lados.utilities.EmailValidator
import org.nullgroup.lados.utilities.NotEmptyValidator
import org.nullgroup.lados.utilities.PasswordValidator
import org.nullgroup.lados.utilities.PhoneNumberValidator
import org.nullgroup.lados.viewmodels.events.RegisterScreenEvent
import org.nullgroup.lados.viewmodels.states.RegisterScreenState
import org.nullgroup.lados.viewmodels.states.ResourceState
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val emailAuth: EmailAuthRepository,
) : ViewModel() {
    var registerState = MutableStateFlow<ResourceState<User>>(ResourceState.Idle)
        private set

    fun validateNotEmpty(data: String): Boolean {
        val validator = NotEmptyValidator(data)
        return validator.validate()
    }

    fun validateEmail(email: String): Boolean {
        val validator = EmailValidator(email)
        return validator.validate()
    }

    fun validatePassword(password: String): Boolean {
        val validator = PasswordValidator(password)
        return validator.validate()
    }

    private fun validateAll(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ): Boolean {
        val firstNameValidator = NotEmptyValidator(firstName)
            .setNext(NotEmptyValidator(lastName))
            .setNext(EmailValidator(email))
            .setNext(PasswordValidator(password))

        return firstNameValidator.validate()
    }

    private fun handleSignUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ) {
        if (!validateAll(firstName, lastName, email, password)) {
            registerState.value = ResourceState.Error("Please fill all fields and correct")
            return
        }

        viewModelScope.launch {
            try {
                emailAuth.signUp("$firstName $lastName", email, password).let {
                    registerState.value = it
                }
            } catch (e: Exception) {
                registerState.value = ResourceState.Error(e.message)
            }
        }
    }

    private fun handleLogin(email: String, password: String) {
        viewModelScope.launch {
            try {
                emailAuth.signIn(email, password).let {
                    registerState.value = it
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

            is RegisterScreenEvent.HandleLogin -> {
                handleLogin(event.email, event.password)
            }
        }
    }
}