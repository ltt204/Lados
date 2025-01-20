package org.nullgroup.lados.viewmodels.admin

import android.util.Log
import androidx.annotation.FloatRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import org.nullgroup.lados.screens.admin.userManagement.FilterState
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _usersUIState = MutableStateFlow<UsersUiState>(UsersUiState.Loading)
    val usersUIState = _usersUIState.asStateFlow()

    private val _originalUsers = MutableStateFlow<List<User>>(emptyList())

    init {
        fetchUsers()
    }

    private fun fetchUsers(){
        viewModelScope.launch {
            try {
                val getUsersResult = userRepository.getAllUsersFromFirestore()
                _usersUIState.value = UsersUiState.Success(getUsersResult.getOrNull() ?: emptyList())
                _originalUsers.value= getUsersResult.getOrNull() ?: emptyList()
            } catch (e: Exception) {
                Log.d("HomeViewModel", "fetchUsers: ${e.message}")
                _usersUIState.value = UsersUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun updateUserByEmail(id: String, role: String, isActive: Boolean): Boolean{
        var check=true
        Log.d("UserManagementViewModel", "updateUserByEmail: $id, $role, $isActive")
        viewModelScope.launch {
            try {
                val updateRoleResult = userRepository.updateUserRole(id, role)
                Log.d("UserManagementViewModel", "updateUserByEmail: ${updateRoleResult.isSuccess}")
                val updateStatusResult = userRepository.updateUserStatus(id, isActive)
                Log.d("UserManagementViewModel", "updateUserByEmail: ${updateStatusResult.isSuccess} ${updateStatusResult.isSuccess}")
            } catch (e: Exception) {
                Log.d("UserManagementViewModel", "UpdateUserByEmail: ${e.message}")
                check=false
            }
        }
        return check;
    }

    fun searchUsers(query: String) {
        resetUsers()
        val currentState = _usersUIState.value
        if (currentState is UsersUiState.Success) {
            Log.d("UserManagementViewModel", "searchUsers: $query, ${query.isEmpty()}, ${query.isBlank()}")
            _usersUIState.value = UsersUiState.Success(
                users = if (query.isEmpty() || query.isBlank()) currentState.users else currentState.users.filter { it.name.contains(query, ignoreCase = true) }
            )
            Log.d("UserManagementViewModel", "searchUsers: ${_usersUIState.value}")
        }
    }

    private fun filterUsersByRole(role: String){
        val currentState = _usersUIState.value
        if (currentState is UsersUiState.Success) {
            _usersUIState.value = UsersUiState.Success(
                users = currentState.users.filter { it.role == role }
            )
        }
    }

    private fun sortUsersByNameAscending(){
        val currentState = _usersUIState.value
        if (currentState is UsersUiState.Success) {
            _usersUIState.value = UsersUiState.Success(
                users = currentState.users.sortedBy { it.name }
            )
        }
    }

    private fun sortUsersByNameDescending(){
        val currentState = _usersUIState.value
        if (currentState is UsersUiState.Success) {
            _usersUIState.value = UsersUiState.Success(
                users = currentState.users.sortedByDescending { it.name }
            )
        }
    }

    private fun sortUsersByEmailAscending() {
        val currentState = _usersUIState.value
        if (currentState is UsersUiState.Success) {
            _usersUIState.value = UsersUiState.Success(
                users = currentState.users.sortedBy { it.email }
            )
        }
    }

    private fun sortUsersByEmailDescending() {
        val currentState = _usersUIState.value
        if (currentState is UsersUiState.Success) {
            _usersUIState.value = UsersUiState.Success(
                users = currentState.users.sortedByDescending { it.email }
            )
        }
    }

    private fun filterUsersByStatus(status: Boolean) {
        val currentState = _usersUIState.value
        if (currentState is UsersUiState.Success) {
            _usersUIState.value = UsersUiState.Success(
                users = currentState.users.filter { it.isActive == status }
            )
        }
    }

    private fun resetUsers(){
        _usersUIState.value = UsersUiState.Success(_originalUsers.value)
    }

    fun filterUsers(filterState: FilterState){
        resetUsers()

        Log.d("UserManagementViewModel", "filterUsers: $filterState")
        Log.d("UserManagementViewModel", "filterUsers: ${_usersUIState.value}")

        if (filterState.searchText != null){
            searchUsers(filterState.searchText!!)
        }

        if (filterState.selectedRole != null){
            filterUsersByRole(filterState.selectedRole!!.uppercase())
        }

        if (filterState.selectedStatus != null){
            filterUsersByStatus(filterState.selectedStatus!!)
        }

        if (filterState.userNameSort != null){
            when (filterState.userNameSort) {
                "User Name to A - Z" -> sortUsersByNameAscending()
                "User Name to Z - A" -> sortUsersByNameDescending()
            }
        }

        if (filterState.emailSort != null){
            when (filterState.emailSort) {
                "Email to A - Z" -> sortUsersByEmailAscending()
                "Email to Z - A" -> sortUsersByEmailDescending()
            }
        }
    }
}

sealed class UsersUiState {
    data object Loading : UsersUiState()
    data class Success(val users: List<User>) : UsersUiState()
    data class Error(val message: String) : UsersUiState()
}