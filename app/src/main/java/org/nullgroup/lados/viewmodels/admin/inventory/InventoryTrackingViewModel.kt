package org.nullgroup.lados.viewmodels.admin.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.remote.models.ProductRemoteModel
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.utilities.toLocalProduct
import javax.inject.Inject

@HiltViewModel
class InventoryTrackingViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _inventoryItems = MutableStateFlow<List<Product>>(emptyList())
    val inventoryItems: StateFlow<List<Product>> = _inventoryItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingAll = MutableStateFlow(false)
    val isLoadingAll: StateFlow<Boolean> = _isLoadingAll.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _isLoadingAll.value = true
            val response = withContext(Dispatchers.IO) {
                productRepository.getAllProductsFromFireStore()
            }
            if (response.isSuccess) {
                response.getOrNull()?.let {
                    val localeProduct = withContext(Dispatchers.Default) {
                        it.map { product ->
                            product.toLocalProduct()
                        }
                    }
                    _products.value = localeProduct
                    _inventoryItems.value = localeProduct.filter {
                        it.variants.sumOf { variant -> variant.quantityInStock } > 10
                    }
                }
            }
            _isLoadingAll.value = false
        }
    }

    fun loadProducts(range: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            when (range) {
                1 -> {
                    _inventoryItems.value = _products.value.filter {
                        it.variants.sumOf { variant -> variant.quantityInStock } > 10
                    }
                }
                2 -> {
                    _inventoryItems.value = _products.value.filter {
                        it.variants.sumOf { variant -> variant.quantityInStock } in 1..10
                    }
                }
                3 -> {
                    _inventoryItems.value = _products.value.filter {
                        it.variants.sumOf { variant -> variant.quantityInStock } == 0
                    }
                }
                else -> {
                    _inventoryItems.value = emptyList()
                }
            }
        }
        _isLoading.value = false
    }

}