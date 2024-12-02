package org.nullgroup.lados.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.repositories.interfaces.CategoryRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
): ViewModel(){
    private val _categories = MutableStateFlow<List<Category>>(mutableListOf())
    val categories = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            fetchCategories()
        }
    }

    private suspend fun fetchCategories() {
        _isLoading.value=true
        val result=categoryRepository.getAllCategoriesFromFireStore()
        _isLoading.value=false

        if (result.isFailure){
            _error.value=result.exceptionOrNull()?.message
            return
        }
        _categories.value=result.getOrNull() ?: emptyList()

    }
}