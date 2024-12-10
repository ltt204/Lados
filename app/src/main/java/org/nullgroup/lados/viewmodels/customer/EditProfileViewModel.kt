package org.nullgroup.lados.viewmodels.customer

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.models.UserProfilePicture
import org.nullgroup.lados.data.repositories.interfaces.ImageRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import org.nullgroup.lados.viewmodels.customer.ProfilePictureUiState.Loading
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    val isInfoChanged = mutableStateOf(false)

    var userUiState: MutableState<UserUiState> = mutableStateOf(UserUiState.Loading)
        private set

    private var userProfilePicture = mutableStateOf(UserProfilePicture())

    var profilePictureUiState: MutableState<ProfilePictureUiState> = mutableStateOf(ProfilePictureUiState.Initial(""))

    init {
        loadUser()
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            if (userUiState.value is UserUiState.Success) {
                val user = (userUiState.value as UserUiState.Success).user
                userUiState.value = UserUiState.Loading
                try {
                    if (isInfoChanged.value) {
                        profilePictureUiState.value = Loading

                        try {
                            imageRepository.deleteImage(
                                child = "users",
                                fileName = user.email,
                                extension = "jpg"
                            )

                            val firebaseStorageUrl = imageRepository.uploadImage(
                                userProfilePicture.value.image,
                                userProfilePicture.value.child,
                                userProfilePicture.value.fileName,
                                userProfilePicture.value.extension
                            )

                            delay(500)
                            profilePictureUiState.value = ProfilePictureUiState.Success(firebaseStorageUrl)
                        } catch (e: Exception) {
                            profilePictureUiState.value = ProfilePictureUiState.Error(e.message ?: "An error occurred")
                        }

                        user.avatarUri = (profilePictureUiState.value as ProfilePictureUiState.Success).uri
                        userRepository.updateUser(user)
                    } else {
                        userRepository.updateUser(user)
                        profilePictureUiState.value = ProfilePictureUiState.Success(user.avatarUri)
                        userUiState.value = UserUiState.Success(user)
                    }
                    Log.d("EditProfileViewModel", "User : $user")
                } catch (e: Exception) {
                    userUiState.value = UserUiState.Error(e.message ?: "An error occurred")
                }
            }
        }
    }

    fun onProfilePictureChanged(
        uri: String,
        image: ByteArray
    ) {
        viewModelScope.launch {
            if (userUiState.value is UserUiState.Success) {
                val user = (userUiState.value as UserUiState.Success).user
                userUiState.value = UserUiState.Success(user.copy(avatarUri = uri))

                userProfilePicture.value = UserProfilePicture(
                    image = image,
                    child = "users",
                    fileName = user.email,
                    extension = "jpg"
                )

                isInfoChanged.value = true
            }
        }
    }

    fun onNameChanged(name: String) {
        viewModelScope.launch {
            delay(500)
            if (userUiState.value is UserUiState.Success) {
                val user = (userUiState.value as UserUiState.Success).user
                userUiState.value = UserUiState.Success(user.copy(name = name))
            }
        }
    }

    fun onPhoneChanged(phone: String) {
        viewModelScope.launch {
            delay(500)
            if (userUiState.value is UserUiState.Success) {
                val user = (userUiState.value as UserUiState.Success).user
                userUiState.value = UserUiState.Success(user.copy(phoneNumber = phone))
            }
        }
    }

    private fun loadUser() {
        userUiState.value = UserUiState.Loading
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                userUiState.value = UserUiState.Success(currentUser)
                profilePictureUiState.value = ProfilePictureUiState.Initial(currentUser.avatarUri)
                Log.d("EditProfileViewModel", "User profile picture: ${currentUser.avatarUri}")
            } catch (e: Exception) {
                userUiState.value = UserUiState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class UserUiState {
    data object Loading : UserUiState()
    data class Success(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()
}

sealed class ProfilePictureUiState {
    data class Initial(val uri: String) : ProfilePictureUiState()
    data class Success(val uri: String) : ProfilePictureUiState()
    data object Loading : ProfilePictureUiState()
    data class Error(val message: String) : ProfilePictureUiState()
}