package org.nullgroup.lados.data.repositories.interfaces

import androidx.activity.ComponentActivity
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.viewmodels.states.ResourceState

interface FacebookAuthRepository {
    suspend fun signIn(activity: ComponentActivity): ResourceState<User>
    suspend fun signOut(): ResourceState<Boolean>
}