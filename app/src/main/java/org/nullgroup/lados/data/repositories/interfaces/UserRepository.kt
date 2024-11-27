package org.nullgroup.lados.data.repositories.interfaces

import org.nullgroup.lados.data.models.User

interface UserRepository {
    suspend fun saveUserToFirestore(user: User)
    suspend fun getUserFromFirestore(email: String): Result<User>
    suspend fun getAllUsersFromFirestore(): Result<List<User>>
    suspend fun getUserRole(email: String): Result<String>
    suspend fun updateUserRole(email: String, role: String): Result<Boolean>
    suspend fun deleteUser(email: String): Result<Boolean>
}