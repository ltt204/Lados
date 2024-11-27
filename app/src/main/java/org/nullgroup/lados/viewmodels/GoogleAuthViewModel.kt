package org.nullgroup.lados.viewmodels

import android.content.Intent
import android.content.IntentSender
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.nullgroup.lados.data.models.SignInResult
import org.nullgroup.lados.data.models.SignInState
import org.nullgroup.lados.data.repositories.interfaces.GoogleAuthRepository
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(
    private val googleAuth: GoogleAuthRepository
) : ViewModel() {
    var state by mutableStateOf(SignInState())
        private set

    fun onSignInResult(result: SignInResult) {
        state = state.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        )
    }

    suspend fun signIn() : IntentSender? {
        return googleAuth.signIn()
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        return googleAuth.signInWithIntent(intent)
    }

    suspend fun signOut() {
        googleAuth.signOut()
        state = state.copy(
            isSignInSuccessful = false,
            signInError = null
        )
    }

    suspend fun getSignedInUser() = googleAuth.getSignedInUser()
}