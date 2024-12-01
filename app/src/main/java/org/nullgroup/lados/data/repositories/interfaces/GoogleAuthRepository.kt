package org.nullgroup.lados.data.repositories.interfaces

import android.content.Intent
import android.content.IntentSender
import dagger.Module
import org.nullgroup.lados.data.models.SignInResult
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.viewmodels.states.ResourceState

interface GoogleAuthRepository {
    suspend fun signIn(): IntentSender?
    suspend fun signInWithIntent(intent: Intent): ResourceState<User>
    suspend fun signOut(): ResourceState<Boolean>
}