package org.nullgroup.lados.data.repositories.interfaces

import android.content.Context
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.viewmodels.common.states.ResourceState

interface AuthRepository {
    suspend fun signInWithPassword(email: String, password: String): ResourceState<User>
    suspend fun signInWithGoogle(context: Context): ResourceState<User>
    suspend fun autoSignIn(): ResourceState<User>
    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        phone: String,
        context: Context,
    ): ResourceState<User>
    suspend fun signOut(): ResourceState<Boolean>
    suspend fun resetPassword(email: String): ResourceState<Boolean>
    suspend fun checkEmailExist(email: String): ResourceState<Boolean>
    suspend fun checkEmailVerify(): Boolean
}