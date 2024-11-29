package org.nullgroup.lados.viewmodels.events

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.navigation.NavController

sealed class LoginScreenEvent {
    data class HandleEnterEmail(val email: String) : LoginScreenEvent()
    data class HandleEnterPassword(val password: String) : LoginScreenEvent()
    data class HandleForgotPassword(val navController: NavController) : LoginScreenEvent()
    data class HandleSignUp(val navController: NavController) : LoginScreenEvent()
    data class HandleLogInWithGoogle(val launcher: ActivityResultLauncher<IntentSenderRequest>) :
        LoginScreenEvent()

    data class HandleLogInWithFacebook(val activity: ComponentActivity) : LoginScreenEvent()
}