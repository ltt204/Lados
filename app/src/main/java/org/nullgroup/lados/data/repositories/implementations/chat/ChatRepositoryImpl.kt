package org.nullgroup.lados.data.repositories.implementations.chat

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import org.nullgroup.lados.data.models.ChatRoom
import org.nullgroup.lados.data.models.Message
import org.nullgroup.lados.data.repositories.interfaces.chat.ChatRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChatRepositoryImpl(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
) : ChatRepository {
    override fun createOrGetChatRoom(customerId: String, onComplete: (String) -> Unit) {
        database.reference.child("chat_rooms")
            .orderByChild("customerId")
            .equalTo(customerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val room: ChatRoom? =
                        snapshot.children.firstOrNull()?.getValue(ChatRoom::class.java)

                    if (room != null) {
                        onComplete(room.id)
                    } else {
                        createNewChatRoom(customerId, onComplete)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatRepository", "Error getting chat room", error.toException())
                }
            })
    }

    override fun createNewChatRoom(customerId: String, onComplete: (String) -> Unit) {
        val chatRoomId = database.reference.child("chat_rooms").push().key ?: return

        val chatRoom = ChatRoom(
            id = chatRoomId,
            customerId = customerId,
        )

        database.reference.child("chat_rooms")
            .child(chatRoomId)
            .setValue(chatRoom.toMap())
            .addOnSuccessListener {
                onComplete(chatRoomId)
            }
    }

    override fun getChatRoomForCustomer(customerId: String): Flow<ChatRoom> = callbackFlow {
        val listener = database.reference.child("chat_rooms")
            .orderByChild("customerId")
            .equalTo(customerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatRoom = snapshot.children.firstOrNull()?.getValue(ChatRoom::class.java)
                    chatRoom?.let {
                        trySend(chatRoom)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatRepository", "Error getting chat room ${error.message}")
                }
            })

        awaitClose {
            database.reference.removeEventListener(listener)
        }
    }

    override fun getChatRoomsForStaff(): Flow<List<ChatRoom>> = callbackFlow {
        val listener = database.reference.child("chat_rooms")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatRooms = snapshot.children.mapNotNull {
                        it.getValue(ChatRoom::class.java)
                    }
                    trySend(chatRooms)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatRepository", "Error getting chat rooms ${error.message}")
                }
            })

        awaitClose {
            database.reference.removeEventListener(listener)
        }
    }

    override suspend fun uploadImage(uri: Uri, chatId: String, messageId: String): Result<String> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val imageRef = storage.reference.child("chat_images/$chatId/$messageId.jpg")
                val uploadTask = imageRef.putFile(uri)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }
                    .addOnSuccessListener { downloadUri ->
                        continuation.resume(Result.success(downloadUri.toString()))
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(Result.failure(e))
                    }
            }
        }

    override suspend fun sendMessage(message: Message, chatId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                database.reference.child("chats")
                    .child(chatId)
                    .child("messages")
                    .setValue(message.toMap())
                    .addOnSuccessListener {
                        continuation.resume(Result.success(Unit))
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(Result.failure(e))
                    }
            }
        }

    override fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val messagesRef = database.reference.child("chats")
            .child(chatId)
            .child("messages")
            .orderByChild("timestamp")

        val listener = messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = snapshot.children.mapNotNull {
                    it.getValue(Message::class.java)
                }
                trySend(messageList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.run { toException() })
            }
        })

        awaitClose {
            messagesRef.removeEventListener(listener)
        }
    }

    override fun generateMessageId(chatId: String): String? =
        database.reference.child("chats").child(chatId).child("messages").push().key

    override fun getCurrentUserId(): String? = auth.currentUser?.uid
}