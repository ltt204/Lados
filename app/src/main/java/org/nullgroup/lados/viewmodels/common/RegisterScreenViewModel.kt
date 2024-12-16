package org.nullgroup.lados.viewmodels.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.EmailAuthRepository
import org.nullgroup.lados.utilities.EmailValidator
import org.nullgroup.lados.utilities.NotEmptyValidator
import org.nullgroup.lados.utilities.PasswordValidator
import org.nullgroup.lados.utilities.PhoneNumberValidator
import org.nullgroup.lados.viewmodels.common.states.RegisterScreenStepState
import org.nullgroup.lados.viewmodels.common.states.ResourceState
import org.nullgroup.lados.viewmodels.common.events.RegisterScreenEvent
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val emailAuth: EmailAuthRepository,
) : ViewModel() {
    var registerState = MutableStateFlow<ResourceState<User>>(ResourceState.Idle)
        private set
    var registerStepState =
        MutableStateFlow<RegisterScreenStepState>(RegisterScreenStepState.EnterInfo)
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

    fun validatePhoneNumber(phone: String): Boolean {
        val validator = PhoneNumberValidator(phone)
        return validator.validate()
    }

    private fun validateAll(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
    ): Boolean {
        val firstNameValidator = NotEmptyValidator(firstName)
            .setNext(NotEmptyValidator(lastName))
            .setNext(EmailValidator(email))
            .setNext(PasswordValidator(password))
            .setNext(PhoneNumberValidator(phone))

        return firstNameValidator.validate()
    }

    private fun handleSignUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
        context: Context,
    ) {
        if (!validateAll(firstName, lastName, email, password, phone)) {
            registerState.value = ResourceState.Error("Please fill all fields and correct")
            return
        }

        viewModelScope.launch {
            try {
                emailAuth.signUp("$firstName $lastName", email, password, phone, context).let {
                    registerState.value = it
                    when (it) {
                        is ResourceState.Error -> {}
                        ResourceState.Idle -> {}
                        ResourceState.Loading -> {}
                        is ResourceState.Success -> {
                            registerStepState.value = RegisterScreenStepState.Notification(email)
                        }
                    }
                }
            } catch (e: Exception) {
                registerState.value = ResourceState.Error(e.message)
            }
        }
    }

    private fun handleBackLogin(navController: NavController) {
        registerStepState.value = RegisterScreenStepState.BackLogin
        navController.popBackStack("login", false)
    }

    fun handleEvent(event: RegisterScreenEvent) {
        registerState.value = ResourceState.Loading

        when (event) {
            is RegisterScreenEvent.HandleSignUp -> {
                handleSignUp(
                    event.firstName,
                    event.lastName,
                    event.email,
                    event.password,
                    event.phone,
                    event.context,
                )
            }

            is RegisterScreenEvent.HandleBackLogin -> {
                handleBackLogin(event.navController)
            }
        }
    }
}