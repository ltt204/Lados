package org.nullgroup.lados.viewmodels.staff

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Message
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.chat.ChatRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import org.nullgroup.lados.screens.Screen.Staff.ChatWithCustomerScreen.CHAT_ID_ARG
import javax.inject.Inject

@HiltViewModel
class StaffChatWithCustomerViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val chatId = checkNotNull(savedStateHandle.get<String>(CHAT_ID_ARG))
    var messageUiState = MutableStateFlow<MessageUiState>(MessageUiState.Loading)
        private set

    lateinit var currentUser: User
        private set

    var chatWith: Pair<String, String> = Pair("", "")
        private set

    init {
        fetchChatRoom()
    }

    private fun fetchChatRoom() {
        viewModelScope.launch {
            currentUser = userRepository.getCurrentUser()
            repository.observeMessages(chatId)
                .flowOn(Dispatchers.IO)
                .catch {
                    messageUiState.value = MessageUiState.Error(it.message ?: "An error occurred")
                }
                .collect { messages ->
                    val senderId = messages.firstOrNull()?.senderId ?: ""
                    val userName = userRepository.getUserName(senderId).getOrNull()
                    val userAvatar = userRepository.getUserAvatar(senderId).getOrNull()
                    chatWith = Pair(userName ?: "", userAvatar ?: "")
                    messageUiState.value = MessageUiState.Success(messages)
                }
        }
    }

}

sealed class MessageUiState {
    data object Loading : MessageUiState()
    data class Error(val message: String) : MessageUiState()
    data class Success(val messages: List<Message>) : MessageUiState()
}