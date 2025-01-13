package org.nullgroup.lados.viewmodels.customer.chat.states

import org.nullgroup.lados.data.models.ChatRoom

sealed class ChatUiState {
    data object Idle : ChatUiState()
    data object Loading : ChatUiState()
    data class Error(val message: String?) : ChatUiState()
}

sealed class ChatRoomUiState {
    data class  Success(var data: ChatRoom) : ChatRoomUiState()
    data object Loading : ChatRoomUiState()
    data class Error(val message: String?) : ChatRoomUiState()
}
