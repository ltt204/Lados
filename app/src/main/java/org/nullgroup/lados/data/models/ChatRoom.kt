package org.nullgroup.lados.data.models

import androidx.annotation.Keep

@Keep
data class ChatRoom(
    val id: String = "",
    val customerId: String = "",
    var lastMessage: String = "",
    var lastMessageTime: Long = System.currentTimeMillis(),
    val lastMessageSendBy: String = "",
    val unreadCount: Int = 0,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "customerId" to customerId,
            "lastMessage" to lastMessage,
            "lastMessageTime" to lastMessageTime,
            "unreadCount" to unreadCount,
        )
    }
}
