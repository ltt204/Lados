package org.nullgroup.lados.viewmodels.customer.chat.states

sealed class ChatUiState {
    data object Idle : ChatUiState()
    data object Loading : ChatUiState()
    data class Error(val message: String?) : ChatUiState()
}
