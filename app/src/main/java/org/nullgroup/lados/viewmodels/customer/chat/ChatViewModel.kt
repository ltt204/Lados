package org.nullgroup.lados.viewmodels.customer.chat

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Message
import org.nullgroup.lados.data.models.MessageType
import org.nullgroup.lados.data.repositories.interfaces.chat.ChatRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import org.nullgroup.lados.screens.Screen.Staff.ChatWithCustomerScreen.CHAT_ROOM_ID_ARG
import org.nullgroup.lados.utilities.capitalizeWords
import org.nullgroup.lados.viewmodels.customer.chat.events.ChatScreenEvent
import org.nullgroup.lados.viewmodels.customer.chat.states.ChatRoomUiState
import org.nullgroup.lados.viewmodels.customer.chat.states.ChatUiState
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val chatRoomId = savedStateHandle.get<String>(CHAT_ROOM_ID_ARG)

    var messages = MutableStateFlow<List<Message>>(emptyList())
        private set

    var uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
        private set

    private var chatId: String = ""

    init {
        if (!chatRoomId.isNullOrEmpty()) {
            Log.d("ChatViewModel:init", "ChatRoomId: $chatRoomId")
            chatId = chatRoomId
            observeMessages(chatId)
        } else {
            repository.createOrGetChatRoom(
                repository.getCurrentUserId() ?: ""
            ) { chatId ->
                this.chatId = chatId
                observeMessages(chatId)
            }
        }
    }

    fun handleEvent(event: ChatScreenEvent) {
        when (event) {
            is ChatScreenEvent.SendImage -> {
                Log.d("ChatViewModel:handleEvent", "SendImage")
                sendImageMessage(event.uri, event.context)
            }

            is ChatScreenEvent.SendProduct -> {
                Log.d("ChatViewModel:handleEvent", "SendProduct")
                sendProductMessage(event.productId)
            }

            is ChatScreenEvent.SendText -> {
                Log.d("ChatViewModel:handleEvent", "SendText")
                sendTextMessage(event.content)
            }
        }
    }

    fun getCurrentUserId(): String = repository.getCurrentUserId() ?: ""

    private fun sendTextMessage(content: String) {
        val messageId = repository.generateMessageId(chatId) ?: return
        val currentUserId = repository.getCurrentUserId() ?: return

        val message = Message(
            id = messageId,
            senderId = currentUserId,
            content = content,
            type = MessageType.TEXT
        )
        viewModelScope.launch {
            try {
                repository.sendMessage(message, chatId)
                    .onFailure { e ->
                        uiState.value = ChatUiState.Error(e.message)
                    }
                    .onSuccess {
                        Log.d(
                            "ChatViewModel:sendTextMessage",
                            "Sender: ${message.senderId} \n Content ${message.content}"
                        )
                        repository.updateLastMessage(chatId, content)
                    }
            } catch (e: Exception) {
                uiState.value = ChatUiState.Error(e.message)
            }
        }
    }

    private fun sendImageMessage(uri: Uri, context: Context) {
        val messageId = repository.generateMessageId(chatId) ?: return
        val currentUserId = repository.getCurrentUserId() ?: return

        viewModelScope.launch {
            uiState.value = ChatUiState.Loading
            try {
                repository.uploadImage(uri, chatId, messageId, context)
                    .onSuccess { imageUrl ->
                        val message = Message(
                            id = messageId,
                            senderId = currentUserId,
                            imageUrl = imageUrl,
                            type = MessageType.IMAGE
                        )

                        Log.d(
                            "ChatViewModel:sendImageMessage",
                            "${message.senderId} ${message.imageUrl}"
                        )

                        repository.sendMessage(message, chatId)
                            .onFailure { e ->
                                uiState.value = ChatUiState.Error(e.message)
                            }
                            .onSuccess {
                                Log.d(
                                    "ChatViewModel:sendImageMessage",
                                    "Sender: ${message.senderId} \n Content ${message.content}"
                                )
                            }
                        val currentUser = userRepository.getCurrentUser()
                        repository.updateLastMessage(chatId, "${currentUser.name.capitalizeWords()} sent an image")
                    }
                    .onFailure { e ->
                        uiState.value = ChatUiState.Error(e.message)
                    }
            } catch (e: Exception) {
                uiState.value = ChatUiState.Error(e.message)
            } finally {
                uiState.value = ChatUiState.Idle
            }
        }
    }

    private fun sendProductMessage(productId: String) {
        val messageId = repository.generateMessageId(chatId) ?: return
        val currentUserId = repository.getCurrentUserId() ?: return

        val message = Message(
            id = messageId,
            senderId = currentUserId,
            productId = productId,
            type = MessageType.PRODUCT,
        )

        viewModelScope.launch {
            try {
                repository.sendMessage(message, chatId)
                    .onFailure { e ->
                        uiState.value = ChatUiState.Error(e.message)
                    }
            } catch (e: Exception) {
                uiState.value = ChatUiState.Error(e.message)
            }
        }
    }

    private fun observeMessages(chatId: String) {
        viewModelScope.launch {
            repository.observeMessages(chatId)
                .catch { e ->
                    uiState.value = ChatUiState.Error(e.message)
                }
                .collect { messageList ->
                    messages.value = messageList
                }
        }
    }
}