package org.nullgroup.lados.data.models

data class Message(
    val id: String = "",
    val senderId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String? = null,
    val productId: String? = null,
    val type: MessageType = MessageType.TEXT,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "senderId" to senderId,
            "content" to content,
            "timestamp" to timestamp,
            "imageUrl" to imageUrl,
            "productId" to productId,
            "type" to type,
        )
    }
}

enum class MessageType {
    TEXT,
    IMAGE,
    PRODUCT,
}
