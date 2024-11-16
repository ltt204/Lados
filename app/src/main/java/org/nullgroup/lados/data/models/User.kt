package org.nullgroup.lados.data.models

import android.location.Address

data class  User (
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val phoneNumber: String = "",
    val address: String = ""
)

enum class UserRole {
    CUSTOMER,
    STAFF,
    ADMIN
}