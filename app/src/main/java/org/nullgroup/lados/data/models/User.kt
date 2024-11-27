package org.nullgroup.lados.data.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val phoneNumber: String = "",
    val address: List<String> = emptyList(),
)

enum class UserRole {
    CUSTOMER,
    STAFF,
    ADMIN
}