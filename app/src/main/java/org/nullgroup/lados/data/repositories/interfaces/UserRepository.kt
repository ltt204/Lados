package org.nullgroup.lados.data.repositories.interfaces

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.User

interface UserRepository {
    fun getCurrentUserFlow(): Flow<User>
    suspend fun addUserToFirestore(user: User)
    suspend fun saveUserToFirestore(user: User)
    suspend fun getUserFromFirestore(id: String): Result<User>
    suspend fun getAllUsersFromFirestore(): Result<List<User>>
    suspend fun getUserRole(id: String): Result<String>
    suspend fun deleteUser(id: String): Result<Boolean>
    suspend fun updateUser(user: User): Result<Boolean>
    suspend fun getCurrentUser(): User
    suspend fun updateUserRole(email: String, role: String): Result<Boolean>
    suspend fun signOut(): Result<Boolean>
}
