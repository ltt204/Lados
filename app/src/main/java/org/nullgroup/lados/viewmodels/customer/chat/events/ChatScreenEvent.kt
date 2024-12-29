package org.nullgroup.lados.viewmodels.customer.chat.events

import android.net.Uri

sealed class ChatScreenEvent {
    data class SendText(
        val content: String,
    ) : ChatScreenEvent()

    data class SendImage(
        val uri: Uri,
    ) : ChatScreenEvent()

    data class SendProduct(
        val productId: String,
    ) : ChatScreenEvent()
}