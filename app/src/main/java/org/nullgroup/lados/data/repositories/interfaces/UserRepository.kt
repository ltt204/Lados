package org.nullgroup.lados.data.repositories.interfaces

import org.nullgroup.lados.data.models.User

interface UserRepository {
    suspend fun addUserToFirestore(user: User)
    suspend fun saveUserToFirestore(user: User)
    suspend fun getUserFromFirestore(id: String): Result<User>
    suspend fun getAllUsersFromFirestore(): Result<List<User>>
    suspend fun getUserRole(id: String): Result<String>
    suspend fun updateUserRole(id: String, role: String): Result<Boolean>
    suspend fun deleteUser(id: String): Result<Boolean>
}