package org.nullgroup.lados.viewmodels.staff

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.implementations.common.RecentUserSearchPreferencesRepository
import org.nullgroup.lados.data.repositories.interfaces.chat.ChatRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class SearchChatViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val recentUserSearchPreferencesRepository: RecentUserSearchPreferencesRepository
) : ViewModel() {
    private val TAG = "SearchChatViewModel"
    var searchChatState = MutableStateFlow<SearchChatState>(SearchChatState.Loading)
        private set

    var recentSearches = MutableStateFlow<List<User>>(emptyList())
        private set

    init {
        fetchRecentSearches()
    }

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

    fun saveRecentSearch(searchValue: String) {
        Log.d(TAG, "Save recent search: $searchValue")
        viewModelScope.launch {
            recentUserSearchPreferencesRepository.modify(searchValue)
        }
    }

    private fun fetchRecentSearches() {
        viewModelScope.launch {
            recentUserSearchPreferencesRepository.recentUserSearches
                .collect {
                    Log.d(TAG, "Recent searches: $it")
                    it.forEach {userId ->
                        val user = userRepository.getUserFromFirestore(userId).getOrNull()
                        if (user != null) {
                            if (recentSearches.value.find { it.id == user.id } == null) {
                                recentSearches.value += user
                            }
                        }
                    }
                }
        }
    }
}

sealed class SearchChatState {
    data object Loading : SearchChatState()
    data class Success(val data: List<User>) : SearchChatState()
    data class Error(val message: String) : SearchChatState()
}