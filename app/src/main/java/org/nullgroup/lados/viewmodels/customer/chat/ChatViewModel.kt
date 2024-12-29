package org.nullgroup.lados.viewmodels.customer.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Message
import org.nullgroup.lados.data.models.MessageType
import org.nullgroup.lados.data.repositories.interfaces.chat.ChatRepository
import org.nullgroup.lados.viewmodels.customer.chat.events.ChatScreenEvent
import org.nullgroup.lados.viewmodels.customer.chat.states.ChatUiState
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
) : ViewModel() {
    var messages = MutableStateFlow<List<Message>>(emptyList())
        private set

    var uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
        private set

    private var chatId: String = ""

    init {
        repository.createOrGetChatRoom(
            repository.getCurrentUserId() ?: ""
        ) { chatId ->
            this.chatId = chatId
        }
    }


    fun handleEvent(event: ChatScreenEvent) {
        when (event) {
            is ChatScreenEvent.SendImage -> {
                sendImageMessage(event.uri, this.chatId)
            }

            is ChatScreenEvent.SendProduct -> {
                sendProductMessage(event.productId, this.chatId)
            }

            is ChatScreenEvent.SendText -> {
                sendTextMessage(event.content, this.chatId)
            }

            is ChatScreenEvent.ObserveMessages -> {
                observeMessages(this.chatId)
            }
        }
    }

    private fun sendTextMessage(content: String, chatId: String) {
        val messageId = repository.generateMessageId(chatId) ?: return
        val currentUserId = repository.getCurrentUserId() ?: return

        val message = Message(
            id = messageId,
            senderId = currentUserId,
            content = content,
            type = MessageType.TEXT
        )

        viewModelScope.launch {
            uiState.value = ChatUiState.Loading
            try {
                repository.sendMessage(message, chatId)
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

    private fun sendImageMessage(uri: Uri, chatId: String) {
        val messageId = repository.generateMessageId(chatId) ?: return
        val currentUserId = repository.getCurrentUserId() ?: return

        viewModelScope.launch {
            uiState.value = ChatUiState.Loading
            try {
                repository.uploadImage(uri, chatId, messageId)
                    .onSuccess { imageUrl ->
                        val message = Message(
                            id = messageId,
                            senderId = currentUserId,
                            content = imageUrl,
                            type = MessageType.IMAGE
                        )

                        repository.sendMessage(message, chatId)
                            .onFailure { e ->
                                uiState.value = ChatUiState.Error(e.message)
                            }
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

    private fun sendProductMessage(productId: String, chatId: String) {
        val messageId = repository.generateMessageId(chatId) ?: return
        val currentUserId = repository.getCurrentUserId() ?: return

        val message = Message(
            id = messageId,
            senderId = currentUserId,
            productId = productId,
            type = MessageType.PRODUCT,
        )

        viewModelScope.launch {
            uiState.value = ChatUiState.Loading
            try {
                repository.sendMessage(message, chatId)
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