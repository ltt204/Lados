package org.nullgroup.lados.viewmodels.common.events

import android.content.Context
import androidx.navigation.NavController


sealed class RegisterScreenEvent {
    data class HandleSignUp(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String,
        val phone: String,
        val context: Context,
    ) : RegisterScreenEvent()
    data class HandleBackLogin(val navController: NavController): RegisterScreenEvent()
}