package org.nullgroup.lados.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.AuthRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    var result = MutableStateFlow<Result<Boolean>>(Result.success(false))
        private set

    var user = MutableStateFlow<String?>(null)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                authRepository.login(email, password).let {
                    result.value = it
                }
                if (result.value.isSuccess) {
                    userRepository.getUserRole(email).let {
                        user.value = it.getOrNull()
                    }
                }
                Log.d("AuthScreenViewModel", "User role: ${user.value}")
            } catch (e: Exception) {
                result.value = Result.failure(e)
            }
        }
    }

    fun signUp(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                authRepository.signUp(fullName, email, password).let {
                    result.value = it
                }
            } catch (e: Exception) {
                result.value = Result.failure(e)
            }
        }
    }
}