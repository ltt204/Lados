package org.nullgroup.lados.viewmodels.staff

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Message
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.chat.ChatRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import org.nullgroup.lados.screens.Screen.Staff.ChatWithCustomerScreen.CHAT_ROOM_ID_ARG
import javax.inject.Inject

@HiltViewModel
class StaffChatWithCustomerViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val chatRoomId = checkNotNull(savedStateHandle.get<String>(CHAT_ROOM_ID_ARG))

    var chatWith: Pair<String, String> = Pair("", "")
        private set
    val msgUiState: StateFlow<MessageUiState> = repository.observeMessages(chatRoomId)
        .map {
            val senderId = it.firstOrNull()?.senderId ?: ""
            val userName = userRepository.getUserName(senderId).getOrNull()
            val userAvatar = userRepository.getUserAvatar(senderId).getOrNull()
            chatWith = Pair(userName ?: "", userAvatar ?: "")
            MessageUiState.Success(it)
        }
        .catch {
            MessageUiState.Error(it.message ?: "An error occurred")
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MessageUiState.Loading
        )

    lateinit var currentStaff: User
        private set

    init {
        fetchChatRoom()
    }

    private fun fetchChatRoom() {
        viewModelScope.launch {
            currentStaff = userRepository.getCurrentUser()
        }
    }
}

sealed class MessageUiState {
    data object Loading : MessageUiState()
    data class Error(val message: String) : MessageUiState()
    data class Success(val messages: List<Message>) : MessageUiState()
}