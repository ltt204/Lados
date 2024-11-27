package org.nullgroup.lados.data.repositories.interfaces

import android.content.Intent
import android.content.IntentSender
import org.nullgroup.lados.data.models.SignInResult
import org.nullgroup.lados.data.models.User

interface GoogleAuthRepository {
    suspend fun signIn(): IntentSender?
    suspend fun signInWithIntent(intent: Intent): SignInResult
    suspend fun signOut(): Unit
    suspend fun getSignedInUser(): User?
}