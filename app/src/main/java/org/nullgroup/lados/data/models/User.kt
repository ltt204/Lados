package org.nullgroup.lados.data.models

import android.location.Address

data class  User (
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val phoneNumber: String,
    val address: Address
)

enum class UserRole {
    CUSTOMER,
    STAFF,
    ADMIN
}