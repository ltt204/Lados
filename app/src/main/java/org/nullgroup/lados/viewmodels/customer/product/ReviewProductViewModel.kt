package org.nullgroup.lados.viewmodels.customer.product

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.models.UserEngagement
import org.nullgroup.lados.data.repositories.interfaces.ProductRepository
import org.nullgroup.lados.data.repositories.interfaces.ReviewProductRepository
import org.nullgroup.lados.screens.Screen
import javax.inject.Inject

@HiltViewModel
class ReviewProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val reviewProductRepository: ReviewProductRepository
) : ViewModel() {

    private val productId: String =
        checkNotNull(savedStateHandle[Screen.Customer.ReviewProductScreen.PRODUCT_ID_ARG]) {
            "Product ID is missing!"
        }

    private val variantId: String =
        checkNotNull(savedStateHandle[Screen.Customer.ReviewProductScreen.VARIANT_ID_ARG]) {
            "Variant ID is missing!"
        }

    private val _currentProduct: StateFlow<Product?> =
        productRepository.getProductByIdFlow(productId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = Product()
            )

    private val _productVariantsState =
        MutableStateFlow<ReviewProductsState>(ReviewProductsState.Loading)
    val productVariantsState: StateFlow<ReviewProductsState> =
        _productVariantsState.asStateFlow()

    init {
        fetchProductWithVariant()
    }

    private fun fetchProductWithVariant() {
        viewModelScope.launch {
            _currentProduct
                .map { product ->
                    product?.let {
                        val variant = it.variants.find { variant -> variant.id == variantId }
                        if (variant != null) {
                            ReviewProductsState.Success(mapOf(it to variant))
                        } else {
                            ReviewProductsState.Error("Variant not found")
                        }
                    } ?: ReviewProductsState.Error("Product not found")
                }
                .flowOn(Dispatchers.IO)
                .catch { throwable ->
                    Log.e("ReviewProductViewModel", "Error fetching product: ${throwable.message}")
                    emit(ReviewProductsState.Error(throwable.message ?: "Unknown error"))
                }
                .collect { state ->
                    _productVariantsState.value = state
                }
        }
    }

    fun sendReview(
        productId: String,
        engagement: UserEngagement
    ){
        viewModelScope.launch {
            reviewProductRepository.sendReview(
                productId =productId,
                engagement = engagement
            )
        }
    }

}

sealed class ReviewProductsState {
    data object Loading : ReviewProductsState()
    data class Success(val product: Map<Product, ProductVariant>) : ReviewProductsState()
    data class Error(val message: String) : ReviewProductsState()
}
