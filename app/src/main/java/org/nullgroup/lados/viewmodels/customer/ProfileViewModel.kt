package org.nullgroup.lados.viewmodels.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
) :ViewModel() {
    val currentUser = userRepository.getCurrentUserFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = User()
        ).also {
            Log.d("ProfileViewModel", "currentUser: $it")
        }

    fun signOut(navController: NavController?) {
        firebaseAuth.signOut()
        navController?.navigate("login") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }
}