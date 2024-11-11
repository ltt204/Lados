package org.nullgroup.lados.data.repositories.interfaces

import org.nullgroup.lados.data.models.User

interface UserRepository {
    suspend fun login(email: String, password: String): Result<Boolean>
    suspend fun signUp(fullName: String, email: String, password: String): Result<Boolean>
    suspend fun saveUserToFirestore(user: User)
    suspend fun getUserRole(email: String): Result<String>
}