package org.nullgroup.lados.data.repositories.interfaces


interface EmailAuthRepository {
    suspend fun signIn(email: String, password: String): Result<Boolean>
    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
    ): Result<Boolean>

    suspend fun signOut(): Result<Boolean>
    suspend fun resetPassword(email: String): Result<Boolean>
}