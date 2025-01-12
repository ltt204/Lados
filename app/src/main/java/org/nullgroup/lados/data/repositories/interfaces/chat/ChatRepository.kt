package org.nullgroup.lados.data.repositories.interfaces.chat

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.ChatRoom
import org.nullgroup.lados.data.models.Message

interface ChatRepository {
    fun createOrGetChatRoom(customerId: String, onComplete: (String) -> Unit)
    fun createNewChatRoom(customerId: String, onComplete: (String) -> Unit)
    fun getChatRoomForCustomer(customerId: String): Flow<ChatRoom>
    fun getChatRoomsForStaff(): Flow<List<ChatRoom>>
    suspend fun uploadImage(uri: Uri, chatId: String, messageId: String, context: Context): Result<String>
    suspend fun sendMessage(message: Message, chatId: String): Result<Unit>
    fun observeMessages(chatId: String): Flow<List<Message>>
    fun generateMessageId(chatId: String): String?
    fun getCurrentUserId(): String?
    suspend fun updateLastMessage(chatRoomId: String, message: String): Result<Boolean>
    suspend fun getChatRoomByUserId(userId: String): Result<ChatRoom>
    suspend fun getChatRoomById(chatRoomId: String): Result<ChatRoom>
    suspend fun removeChatRoom(chatRoomId: String): Result<Boolean>
}