package org.nullgroup.lados.data.models

data class  User (
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
)

enum class UserRole {
    CUSTOMER,
    STAFF,
    ADMIN
}