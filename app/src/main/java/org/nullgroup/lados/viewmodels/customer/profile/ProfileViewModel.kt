package org.nullgroup.lados.viewmodels.customer.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.auth.AuthRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: AuthRepository,
) : ViewModel() {
    val currentUser = userRepository.getCurrentUserFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = User()
        ).also {
            Log.d("ProfileViewModel", "currentUser: $it")
        }

    fun signOut(navController: NavController?) {
        viewModelScope.launch {
            auth.signOut()
            navController?.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
}