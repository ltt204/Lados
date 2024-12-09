package org.nullgroup.lados.data.repositories.interfaces

import android.content.Intent
import android.content.IntentSender
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.viewmodels.common.states.ResourceState

interface GoogleAuthRepository {
    suspend fun signIn(): IntentSender?
    suspend fun signInWithIntent(intent: Intent): ResourceState<User>
    suspend fun signOut(): ResourceState<Boolean>
}