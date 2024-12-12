package org.nullgroup.lados.data.models

import android.net.Uri
import com.google.firebase.firestore.DocumentId
import java.net.URI

data class User(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val phoneNumber: String = "",
    var avatarUri: String = "",
)

data class UserProfilePicture(
    val image: ByteArray = byteArrayOf(),
    val child: String = "users",
    val fileName: String = "",
    val extension: String = ""
)
