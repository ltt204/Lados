package org.nullgroup.lados.data.repositories.interfaces

import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.viewmodels.states.ResourceState


interface EmailAuthRepository {
    suspend fun signIn(email: String, password: String): ResourceState<User>
    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        phone: String,
    ): ResourceState<User>

    suspend fun signOut(): ResourceState<Boolean>
    suspend fun resetPassword(email: String): ResourceState<Boolean>
    suspend fun checkEmailExist(email: String): ResourceState<Boolean>
}