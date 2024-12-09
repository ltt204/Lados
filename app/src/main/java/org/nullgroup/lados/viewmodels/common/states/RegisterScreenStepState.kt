package org.nullgroup.lados.viewmodels.common.states

sealed class RegisterScreenStepState {
    data object EnterInfo: RegisterScreenStepState()
    data class Notification(val email: String): RegisterScreenStepState()
    data object BackLogin: RegisterScreenStepState()
}
