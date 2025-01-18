package org.nullgroup.lados.viewmodels.admin.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.common.ImageRepository
import javax.inject.Inject

val categoriesSortOption = listOf(
    "All",
    "Name: A-Z",
    "Name: Z-A",
    "Newest",
    "Oldest"
)

data class FilterItem(
    val title: String = "",
    val options: List<String> = emptyList()
)

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _categoriesUiState = MutableStateFlow<CategoryManagementUiState>(
        value = CategoryManagementUiState.Success(emptyList())
    )
    val categoriesUiState: StateFlow<CategoryManagementUiState> = _categoriesUiState

    private val _deleteCategory = MutableStateFlow<Boolean>(false)
    val deleteCategory: StateFlow<Boolean> = _deleteCategory

    init {
        getSortedAndFilteredCategories()
    }

    fun extractSortOption(sortOption: String): Pair<String, Boolean> {
        return when (sortOption) {
            "Name: A-Z" -> Pair("categoryName", true)
            "Name: Z-A" -> Pair("categoryName", false)
            "Newest" -> Pair("createdAt", false)
            "Oldest" -> Pair("createdAt", true)
            else -> Pair("categoryName", true)
        }
    }

    fun getSortedAndFilteredCategories(
        filterField: String? = null,
        filterValue: Any? = null,
        sortByField: String = "categoryName",
        ascending: Boolean = true
    ) {
        viewModelScope.launch {
            _categoriesUiState.value = CategoryManagementUiState.Loading

            val result = categoryRepository.getAllSortedAndFilteredCategoriesFromFireStore(
                filterField = filterField,
                filterValue = filterValue,
                sortByField = sortByField,
                ascending = ascending
            )

            if (result.isSuccess) {
                _categoriesUiState.value =
                    CategoryManagementUiState.Success(result.getOrDefault(emptyList()))
            } else {
                _categoriesUiState.value = CategoryManagementUiState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown error"
                )
            }

        }
    }

    fun searchCategories(query: String) {
        viewModelScope.launch {
            _categoriesUiState.value = CategoryManagementUiState.Loading
            val result = categoryRepository.getAllCategoriesFromFireStore()
            Log.d("CategoryManagementViewModel", "searchCategories: $result")
            if (result.isSuccess) {
                val filteredCategories = result.getOrDefault(emptyList()).filter {
                    it.categoryName.contains(query, ignoreCase = true)
                }
                _categoriesUiState.value = CategoryManagementUiState.Success(filteredCategories)
            } else {
                _categoriesUiState.value = CategoryManagementUiState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown error"
                )
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            val result = categoryRepository.deleteCategory(id)
            if (result.isSuccess) {
                _deleteCategory.value = true
                imageRepository.deleteImage(
                    "categories",
                    id,
                    extension = "jpg"
                )
            } else {
                _deleteCategory.value = false
            }
        }
    }
}

sealed class CategoryManagementUiState {
    data object Loading : CategoryManagementUiState()
    data class Success(val categories: List<Category>) : CategoryManagementUiState()
    data class Error(val message: String) : CategoryManagementUiState()
}