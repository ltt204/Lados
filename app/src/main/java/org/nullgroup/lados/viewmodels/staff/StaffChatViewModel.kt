package org.nullgroup.lados.viewmodels.staff

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.ChatRoom
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.chat.ChatRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class StaffChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    val userRepository: UserRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow<StaffChatScreenUiState>(StaffChatScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchMessages()
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            chatRepository.getChatRoomsForStaff()
                .flowOn(Dispatchers.IO)
                .catch { exception ->
                    Log.e("StaffChatViewModel", "Error fetching chat rooms", exception)
                    _uiState.value = StaffChatScreenUiState.Error(message = exception.message.toString())
                }
                .collect { chatRooms ->
                    val messages = chatRooms
                        .sortedByDescending { it.lastMessageTime }
                        .mapNotNull { chatRoom ->
                            userRepository.getUserFromFirestore(chatRoom.customerId).getOrNull()?.let { user ->
                                user to chatRoom
                            }
                        }
                        .toMap()
                    _uiState.value = StaffChatScreenUiState.Success(messages)
                }
        }
    }
}

sealed class StaffChatScreenUiState {
    class Success(var data: Map<User, ChatRoom>) : StaffChatScreenUiState()
    data object Loading : StaffChatScreenUiState()
    class Error(var message: String) : StaffChatScreenUiState()
}