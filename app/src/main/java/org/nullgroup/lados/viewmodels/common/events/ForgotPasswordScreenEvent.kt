package org.nullgroup.lados.viewmodels.common.events

import androidx.navigation.NavController

sealed class ForgotPasswordScreenEvent {
    data class HandleResetPassword(val email: String): ForgotPasswordScreenEvent()
    data class HandleReturnToLogin(val navController: NavController): ForgotPasswordScreenEvent()
}