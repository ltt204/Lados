package org.nullgroup.lados.data.repositories.interfaces

import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.nullgroup.lados.data.repositories.implementations.LoginState

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Boolean>
    suspend fun loginWithGoogle(): Result<Boolean>
    suspend fun loginWithFacebook(activity: ComponentActivity): LoginState
    suspend fun loginWithTwitter(): Result<Boolean>
    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
    ): Result<Boolean>

    suspend fun resetPassword(email: String): Result<Boolean>
    suspend fun logout(): Result<Boolean>
}