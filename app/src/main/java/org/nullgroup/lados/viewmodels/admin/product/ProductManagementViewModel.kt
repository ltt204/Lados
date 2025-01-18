package org.nullgroup.lados.viewmodels.admin.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import javax.inject.Inject

val ratingOptions = listOf(
    "Default",
    "1 - 2",
    "2 - 3",
    "3 - 4",
    "4 - 5"
)

val priceOptions = listOf(
    "Default",
    "100.000 - 200.000 VND",
    "200.000 - 500.000 VND",
    "500.000 - 1.000.000 VND",
    "1.000.000 - 2.000.000 VND",
    "2.000.000 VND + "
)

val sortOptions = listOf(
    "All",
    "Newest",
    "Oldest",
    "Name: A - Z",
    "Name: Z - A",
    "Price: High to Low",
    "Price: Low to High",
    "Rating: High to Low",
    "Rating: Low to High",
    "Stock: High to Low",
    "Stock: Low to High"
)

fun calculateAverageEngagementRating(product: Product): Double {
    val ratings =
        product.engagements.map { it.ratings }
    return if (ratings.isNotEmpty()) {
        ratings.average()
    } else {
        0.0
    }
}

val sortSelectors = mapOf<String, (Product) -> Comparable<*>>(
    "name" to { it.name },
    "createdAt" to { it.createdAt },
    "price" to { it.variants.firstOrNull()?.originalPrice ?: 0.0 },
    "rating" to { calculateAverageEngagementRating(it) },
    "stock" to { it.variants.sumOf { variant -> variant.quantityInStock } }
)

data class FilterItem(
    val title: String = "",
    val options: List<String> = emptyList()
)

val listOfChoice = listOf(
    FilterItem("Sort by", listOf("Name", "Price", "Create Date", "Rating")),
    FilterItem("Color", listOf("Red", "Green", "Blue")),
    FilterItem("Size", listOf("S", "M", "L", "XL"))
)

@HiltViewModel
class ProductManagementScreenViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _editProducts = MutableStateFlow<List<Product>>(emptyList())
    val editProducts: StateFlow<List<Product>> = _editProducts.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        loadProducts()
        loadCategories()

    }

    private fun loadProducts() {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                productRepository.getAllProductsFromFireStore()
            }

            if(response.isSuccess){
                response.getOrNull()?.let {
                    _products.value = it
                    _editProducts.value = it
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                categoryRepository.getAllCategoriesFromFireStore()
            }

            if(response.isSuccess){
                response.getOrNull()?.let {
                    _categories.value += Category(categoryName = "All")
                    _categories.value += it
                }
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _editProducts.value = _products.value.filter { product ->
                product.name.contains(query, ignoreCase = true)
            }
        }
    }

    fun sortAndFilter(
        categoryOption: String,
        sortOption: String,
        priceOption: String,
        ratingOption: String,
    ) {
        viewModelScope.launch {
            val sort = getSortOption(sortOption)
            val priceRange = getRange(priceOption)
            val ratingRange = getRange(ratingOption)
            val category = _categories.value.find { it.categoryName == categoryOption }

            if(category?.categoryId == null || category.categoryId == ""){
                Log.d("View model ", "Yes")
                _editProducts.value = _products.value
            } else {
                _editProducts.value = _products.value.filter { product ->
                    product.categoryId == category.categoryId
                }
                Log.d("View model ", _editProducts.value.toString())
            }

            if(priceRange.first != null && priceRange.second != null){
                _editProducts.value = _editProducts.value.filter { product ->
                    product.variants.first().originalPrice <= priceRange.second!! &&
                            product.variants.first().originalPrice >= priceRange.first!!
                }
            }

            if (ratingRange.first != null && ratingRange.second != null){
                _editProducts.value = _editProducts.value.filter { product ->
                    calculateAverageEngagementRating(product) <= (ratingRange.second
                        ?: Double.MAX_VALUE) &&
                            calculateAverageEngagementRating(product) >= (ratingRange.first ?: 0.0)
                }
            }

            if (sort.first != "all") {
                _editProducts.value = sortByAttribute(
                    _editProducts.value,
                    sort.first,
                    if (sort.second == "ASC") SortDirection.ASC else SortDirection.DESC,
                    attributeSelector = sortSelectors,
                )
            }
        }
    }



    private fun getSortOption(sortOption: String): Pair<String, String> {
        return when (sortOption) {
            "All" -> "all" to "ASC"
            "Newest" -> "createdAt" to "DESC"
            "Oldest" -> "createdAt" to "ASC"
            "Name: A - Z" -> "name" to "ASC"
            "Name: Z - A" -> "name" to "DESC"
            "Price: High to Low" -> "price" to "DESC"
            "Price: Low to High" -> "price" to "ASC"
            "Rating: High to Low" -> "rating" to "DESC"
            "Rating: Low to High" -> "rating" to "ASC"
            "Stock: High to Low" -> "stock" to "DESC"
            "Stock: Low to High" -> "stock" to "ASC"
            else -> throw IllegalArgumentException("Unknown sort option")
        }
    }

    private fun getRange(option: String, defaultMax: Double? = Double.MAX_VALUE): Pair<Double?, Double?> {
        return when {
            option == "Default" -> null to null
            option.contains("+") -> {
                // Tùy chọn như "2.000.000 VND +", chỉ có min
                val min = option.substringBefore(" ").replace(".", "").toDoubleOrNull()
                min to defaultMax
            }

            option.contains("-") -> {
                // Tùy chọn như "100.000 - 200.000 VND" hoặc "1 - 2"
                val parts = option.split("-")
                    .map { it.trim().substringBefore(" ").replace(".", "").toDoubleOrNull() }
                parts[0] to parts[1]
            }

            else -> throw IllegalArgumentException("Unknown option format")
        }
    }

    private fun <T> sortByAttribute(
        list: List<T>,
        attribute: String,
        direction: SortDirection = SortDirection.ASC,
        attributeSelector: Map<String, (T) -> Comparable<*>>
    ): List<T> {
        val selector = attributeSelector[attribute]
            ?: throw IllegalArgumentException("Attribute '$attribute' not found in the selector map")

        return when (direction) {
            SortDirection.ASC -> list.sortedWith(compareBy { selector(it) })
            SortDirection.DESC -> list.sortedWith(compareByDescending { selector(it) })
        }
    }

}

enum class SortDirection {
    ASC, DESC
}