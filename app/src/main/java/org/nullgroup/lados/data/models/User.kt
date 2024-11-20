package org.nullgroup.lados.data.models

data class User(
    val id: Int = 0,
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