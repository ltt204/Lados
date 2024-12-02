package org.nullgroup.lados.data.repositories.interfaces

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.User

interface UserRepository {
    fun getCurrentUserFlow(): Flow<User>

    suspend fun login(email: String, password: String): Result<Boolean>
    suspend fun signUp(fullName: String, email: String, password: String): Result<Boolean>
    suspend fun saveUserToFirestore(user: User)
    suspend fun getUserFromFirestore(email: String): Result<User>
    suspend fun getAllUsersFromFirestore(): Result<List<User>>
    suspend fun getUserRole(email: String): Result<String>
    suspend fun updateUserRole(email: String, role: String): Result<Boolean>
    suspend fun deleteUser(email: String): Result<Boolean>
    suspend fun updateUser(user: User): Result<Boolean>
    suspend fun getCurrentUser(): User
}