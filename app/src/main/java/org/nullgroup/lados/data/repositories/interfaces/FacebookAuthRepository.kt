package org.nullgroup.lados.data.repositories.interfaces

import androidx.activity.ComponentActivity

interface FacebookAuthRepository {
    suspend fun signIn(activity: ComponentActivity): Result<Boolean>
    suspend fun signOut(): Result<Boolean>
}