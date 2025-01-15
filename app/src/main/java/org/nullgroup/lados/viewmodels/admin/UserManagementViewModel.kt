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
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _usersUIState = MutableStateFlow<UsersUiState>(UsersUiState.Loading)
    val usersUIState = _usersUIState.asStateFlow()

    init {
        fetchUsers()
    }

    private fun fetchUsers(){
        viewModelScope.launch {
            try {
                val getUsersResult = userRepository.getAllUsersFromFirestore()
                _usersUIState.value = UsersUiState.Success(getUsersResult.getOrNull() ?: emptyList())
            } catch (e: Exception) {
                Log.d("HomeViewModel", "fetchUsers: ${e.message}")
                _usersUIState.value = UsersUiState.Error(e.message ?: "An error occurred")
            }
        }
    }
    /*
    fun resetProducts() {
        _productUiState.value = ProductUiState.Success(_originalProducts.value)
    }

    fun sortProductsByPriceLowToHigh() {
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            _productUiState.value = ProductUiState.Success(
                products = currentState.products.sortedBy { if (it.hasNoSalePrice()) it.variants[0].originalPrice else it.isProductOnSale().second }
            )
        }
    }

    fun filterProductsByRating(low: Float, high: Float){
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            //Log.d("Product compare", "filterProductsByRating: ${currentState.products.none { (it.getAverageRating() < low) || (it.getAverageRating() > high) }}")
            _productUiState.value = ProductUiState.Success(
                products = currentState.products.filter { !(it.getAverageRating() < low) && !(it.getAverageRating() > high) }
            )
        }
    }

    fun filterProductsByPrice(@FloatRange(from = 0.0) minPrice: Float, @FloatRange(from = 0.0) maxPrice: Float){
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            _productUiState.value = ProductUiState.Success(
                products = currentState.products.filter { if (it.hasNoSalePrice()) {it.variants[0].originalPrice in minPrice..maxPrice} else {
                    it.isProductOnSale().second!! in minPrice..maxPrice} }
            )
        }
    }

    fun filterProductsByCategory(category: Category) {
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            _productUiState.value = ProductUiState.Success(
                products = currentState.products.filter { it.categoryId == category.categoryId }
            )
        }
    }

    fun findCategoryByName(name: String): Category? {
        val currentState = _categoryUiState.value
        if (currentState is CategoryUiState.Success) {
            return currentState.categories.find { it.categoryName == name }
        }
        return null
    }

    fun sortProductsByPriceHighToLow() {
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            _productUiState.value = ProductUiState.Success(
                products = currentState.products.sortedByDescending { if (it.hasNoSalePrice()) it.variants[0].originalPrice else it.isProductOnSale().second }
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
                products = currentState.products.filter { !it.hasNoSalePrice() }
            )
        }
    }

     */
}

sealed class UsersUiState {
    data object Loading : UsersUiState()
    data class Success(val users: List<User>) : UsersUiState()
    data class Error(val message: String) : UsersUiState()
}