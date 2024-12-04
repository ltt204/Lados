package org.nullgroup.lados.data.models

import com.google.firebase.firestore.DocumentId
import android.net.Uri
import java.net.URI

data class User(
    @DocumentId val id: String = "",
    var name: String = "",
    var email: String = "",
    var role: String = "",
    var phoneNumber: String = "",
    var photoUrl: String = "",
    var provider: String = "",
)

data class UserProfilePicture(
    val image: ByteArray = byteArrayOf(),
    val child: String = "users",
    val fileName: String = "",
    val extension: String = ""
)

enum class UserRole {
    CUSTOMER,
    STAFF,
    ADMIN
}