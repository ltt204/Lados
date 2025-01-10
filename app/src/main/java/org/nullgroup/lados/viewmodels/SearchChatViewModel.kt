package org.nullgroup.lados.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.ChatRoom
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.chat.ChatRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class SearchChatViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val TAG = "SearchChatViewModel"
    var searchChatState = MutableStateFlow<SearchChatState>(SearchChatState.Loading)
        private set

    fun onSearching(searchValue: String) {
        viewModelScope.launch {
            searchChatState.value = SearchChatState.Loading
            delay(500)
            val searchResult = userRepository.getUsersByName(searchValue).getOrNull()
            Log.d(TAG, "Result: $searchResult")
            if (searchResult.isNullOrEmpty()) {
                searchChatState.value = SearchChatState.Error(message = "No result matched")
            } else {
                searchChatState.value = SearchChatState.Success(data = searchResult)
            }
        }
    }

    fun getRoomChatByUserId(userId: String, onComplete: (String) -> Unit) {
        searchChatState.value = SearchChatState.Loading
        chatRepository.createOrGetChatRoom(userId, onComplete)
    }
}

sealed class SearchChatState {
    data object Loading : SearchChatState()
    data class Success(val data: List<User>) : SearchChatState()
    data class Error(val message: String) : SearchChatState()
}