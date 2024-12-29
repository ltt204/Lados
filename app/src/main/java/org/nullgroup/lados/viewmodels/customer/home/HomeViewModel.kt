package org.nullgroup.lados.viewmodels.customer.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _categoryUiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryUiState = _categoryUiState.asStateFlow()

    private val _productUiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val productUiState = _productUiState.asStateFlow()

    private val _originalProducts = MutableStateFlow<List<Product>>(emptyList())

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
                val products = getProductsResult.getOrNull() ?: emptyList()
                _originalProducts.value = products
                _productUiState.value = ProductUiState.Success(products)
            } catch (e: Exception) {
                Log.d("HomeViewModel", "fetchProducts: ${e.message}")
                _productUiState.value = ProductUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun resetProducts() {
        _productUiState.value = ProductUiState.Success(_originalProducts.value)
    }

    fun sortProductsByPriceLowToHigh() {
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            _productUiState.value = ProductUiState.Success(
                products = currentState.products.sortedBy { it.variants[0].salePrice }
            )
        }
    }

    fun sortProductsByPriceHighToLow() {
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            _productUiState.value = ProductUiState.Success(
                products = currentState.products.sortedByDescending { it.variants[0].salePrice }
            )
        }
    }

    fun sortProductsByCreatedAt() {
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            _productUiState.value = ProductUiState.Success(
                products = currentState.products.sortedByDescending { it.createdAt }
            )
        }
    }

    fun filterSaleProducts() {
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            _productUiState.value = ProductUiState.Success(
                products = currentState.products.filter { it.variants[0].salePrice!! < 50.0f }
            )
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