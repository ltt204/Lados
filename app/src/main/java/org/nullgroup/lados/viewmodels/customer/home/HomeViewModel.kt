package org.nullgroup.lados.viewmodels.customer.home

import android.util.Log
import androidx.annotation.FloatRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.screens.customer.home.hasNoSalePrice
import org.nullgroup.lados.screens.customer.home.isProductOnSale
import org.nullgroup.lados.screens.customer.product.FilterState
import org.nullgroup.lados.screens.customer.product.getAverageRating
import org.nullgroup.lados.utilities.toLocalProduct
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
                val products = withContext(Dispatchers.IO) {
                    productRepository.getAllProductsFromFireStore().getOrNull().orEmpty()
                }
                val localeProducts = withContext(Dispatchers.Default) {
                    products.map { it.toLocalProduct() }
                }
                _originalProducts.value = localeProducts
                _productUiState.value = ProductUiState.Success(localeProducts)
            } catch (e: Exception) {
                Log.d("HomeViewModel", "fetchProducts: ${e.message}")
                _productUiState.value = ProductUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun filterProducts(filterCriteria: FilterState){
        resetProducts()
        if (filterCriteria.selectedCategories != null){
            filterProductsByCategory(findCategoryByName(filterCriteria.selectedCategories!!)!!)
        }
        if (filterCriteria.isOnSale){
            filterSaleProducts()
        }
        if (filterCriteria.price != null){
            when (filterCriteria.price){
                "Price (Low to High)" -> sortProductsByPriceLowToHigh()
                "Price (High to Low)" -> sortProductsByPriceHighToLow()
            }
        }
        if (filterCriteria.sortBy != null){
            when (filterCriteria.sortBy){
                "Recommended" -> sortProductsByRating()
                "Newest" -> sortProductsByCreatedAt()
            }
        }
        if (filterCriteria.ratingRange != null){
            filterProductsByRating(filterCriteria.ratingRange!!.start, filterCriteria.ratingRange!!.endInclusive)
        }
        if (filterCriteria.pricingRange != null){
            filterProductsByPrice(filterCriteria.pricingRange!!.start, filterCriteria.pricingRange!!.endInclusive)
        }

    }


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

    fun sortProductsByRating() {
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            _productUiState.value = ProductUiState.Success(
                products = currentState.products.sortedByDescending { it.getAverageRating() }
            )
        }
    }

    fun findMaxPrice(): Double {
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            return currentState.products.maxOf { if (it.hasNoSalePrice()) it.variants[0].originalPrice else it.isProductOnSale().second!! }
        }
        return 0.0
    }

    fun findMinPrice(): Double {
        val currentState = _productUiState.value
        if (currentState is ProductUiState.Success) {
            return currentState.products.minOf { if (it.hasNoSalePrice()) it.variants[0].originalPrice else it.isProductOnSale().second!! }
        }
        return 0.0
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