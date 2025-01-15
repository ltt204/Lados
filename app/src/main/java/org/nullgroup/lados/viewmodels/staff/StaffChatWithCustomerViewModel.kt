package org.nullgroup.lados.viewmodels.staff

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.ChatRoom
import org.nullgroup.lados.data.models.Message
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.chat.ChatRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import org.nullgroup.lados.screens.Screen.Staff.ChatWithCustomerScreen.CHAT_ROOM_ID_ARG
import javax.inject.Inject

@HiltViewModel
class StaffChatWithCustomerViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val chatRoomId = checkNotNull(savedStateHandle.get<String>(CHAT_ROOM_ID_ARG)) //

    var chatWith: Pair<String, String> = Pair("", "")
        private set

    var msgUiState = MutableStateFlow<MessageUiState>(MessageUiState.Loading)
        private set
    lateinit var currentStaff: User
        private set

    init {
        fetchChatRoom()
    }

    private fun fetchChatRoom() {
        viewModelScope.launch {
            currentStaff = userRepository.getCurrentUser()

            val chatRoom = chatRepository.getChatRoomById(chatRoomId).getOrNull()
            if (chatRoom == null) {
                MessageUiState.Error("Something is wrong, please try again later.")
            } else {
                Log.d("StaffChatWithCustomerViewModel", "chatRoom: ${chatRoom.lastMessage}")
                chatRepository.observeMessages(chatRoom.id)
                    .flowOn(Dispatchers.IO)
                    .catch {
                        MessageUiState.Error(it.message ?: "An error occurred")
                    }
                    .collect {
                        val customerId = chatRoom.customerId
                        Log.d("StaffChatWithCustomerViewModel", "customerId: $customerId")
                        val userName = userRepository.getUserName(customerId).getOrNull()
                        val userAvatar = userRepository.getUserAvatar(customerId).getOrNull()
                        chatWith = Pair(userName ?: "", userAvatar ?: "")
                        Log.d("StaffChatWithCustomerViewModel", "chatWith: $it")
                        msgUiState.value = MessageUiState.Success(it)
                    }

            }
        }
    }
}

sealed class MessageUiState {
    data object Loading : MessageUiState()
    data class Error(val message: String) : MessageUiState()
    data class Success(val messages: List<Message>) : MessageUiState()
}