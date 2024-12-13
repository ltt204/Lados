package org.nullgroup.lados.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.repositories.interfaces.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.ProductRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _categoryUiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryUiState = _categoryUiState.asStateFlow()
//
//    private val _isLoading = MutableStateFlow(true)
//    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error.asStateFlow()

    private val _productUiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val productUiState = _productUiState.asStateFlow()

    init {
        fetchCategories()
        fetchProducts()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val getCategoriesResult = categoryRepository.getAllCategoriesFromFireStore()

                _categoryUiState.value =
                    CategoryUiState.Success(getCategoriesResult.getOrNull() ?: emptyList())
            } catch (e: Exception) {
                Log.d("HomeViewModel", "fetchCategories: ${e.message}")
                _categoryUiState.value = CategoryUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val getProductsResult = productRepository.getAllProductsFromFireStore()
                _productUiState.value =
                    ProductUiState.Success(getProductsResult.getOrNull() ?: emptyList())
            } catch (e: Exception) {
                Log.d("HomeViewModel", "fetchProducts: ${e.message}")
                _productUiState.value = ProductUiState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class ProductUiState {
    data object Loading : ProductUiState()
    data class Success(val products: List<Product>) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}

sealed class CategoryUiState {
    data object Loading : CategoryUiState()
    data class Success(val categories: List<Category>) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}