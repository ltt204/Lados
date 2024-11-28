package org.nullgroup.lados.viewmodels.customer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var userUisState: MutableState<UserUiState> = mutableStateOf(UserUiState.Loading)
        private set

    init {
        loadUser()
    }

    fun onNameChanged(name: String) {
        viewModelScope.launch {
            delay(500)
            if (userUisState.value is UserUiState.Success) {
                val user = (userUisState.value as UserUiState.Success).user
                userUisState.value = UserUiState.Success(user.copy(name = name))
            }
        }
    }

    fun onPhoneChanged(phone: String) {
        viewModelScope.launch {
            delay(500)
            if (userUisState.value is UserUiState.Success) {
                val user = (userUisState.value as UserUiState.Success).user
                userUisState.value = UserUiState.Success(user.copy(phoneNumber = phone))
            }
        }
    }

    private fun loadUser() {
        userUisState.value = UserUiState.Loading
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                userUisState.value = UserUiState.Success(currentUser)
            } catch (e: Exception) {
                userUisState.value = UserUiState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class UserUiState {
    data object Loading : UserUiState()
    data class Success(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()
}