package org.nullgroup.lados.viewmodels.customer

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.Color
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.Size
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository
import org.nullgroup.lados.data.repositories.interfaces.ProductRepository
import org.nullgroup.lados.screens.customer.product.ProductDetailUiState
import javax.inject.Inject


@HiltViewModel
class ProductDetailScreenViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
) : ViewModel() {

    // Sealed class for different product-related states
    sealed class ProductState {
        data object Loading : ProductState()
        data class Success(val product: Product?) : ProductState()
        data class Error(val message: String) : ProductState()
    }

    // Centralized state management using StateFlow
    private val _productState = MutableStateFlow<ProductState>(ProductState.Loading)
    val productState: StateFlow<ProductState> = _productState.asStateFlow()

    // Detailed UI State for Product Detail Screen
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun getProductById(id: String) {
        viewModelScope.launch {
            _productState.value = ProductState.Loading
            try {
                val result = productRepository.getProductByIdFromFireStore(id)
                result.onSuccess { product ->
                    _productState.value = ProductState.Success(product)

                    product?.let { loadProductDetailsIntoUiState(it) }
                }.onFailure { exception ->
                    _productState.value = ProductState.Error(
                        exception.message ?: "Failed to fetch product"
                    )
                }
            } catch (e: Exception) {
                _productState.value = ProductState.Error(
                    e.message ?: "Network error while fetching product"
                )
            }
        }
    }

    fun addProducts(products: List<Product>) {
        viewModelScope.launch {
            productRepository.addProductsToFireStore(products)
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            try {
                val result = productRepository.addProductToFireStore(product)
                result.onSuccess {
                    Log.d("ProductViewModel", "Product added successfully")
                    // Optional: Refresh product list or trigger a success event
                }.onFailure { exception ->
                    Log.e("ProductViewModel", "Failed to add product: ${exception.message}")
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error adding product: ${e.message}")
            }
        }
    }

    private fun loadProductDetailsIntoUiState(product: Product) {
        val sortedColors = getSortedColors(product)
        val sortedSizes = getSortedSizes(product)

        _uiState.update {
            it.copy(
                product = product,
                sortedColors = sortedColors,
                sortedSizes = sortedSizes,
                selectedSize = sortedSizes.firstOrNull(),
                selectedColor = sortedColors.firstOrNull(),
                isLoading = false
            )
        }
    }

    fun updateSelectedColor(selectedColor: Color) {
        _uiState.update {
            it.copy(
                selectedColor = selectedColor
            )
        }
    }

    fun updateSelectedSize(size: Size) {
        _uiState.update {
            it.copy(
                selectedSize = size
            )
        }
    }

    fun updateQuantity(quantity: Int) {
        _uiState.update {
            it.copy(
                quantity = quantity
            )
        }
    }

    private fun getSortedColors(product: Product): List<Color> {
        return product.variants
            .map { it.color }
            .distinctBy { it.id }
            .sortedBy { it.colorName }
    }

    private fun getSortedSizes(product: Product): List<Size> {
        return product.variants
            .map { it.size }
            .distinctBy { it.id }
            .sortedBy { it.sizeName }
    }

    // TODO: Adjust logic as you wish
    val onAddToCartClicked: (
        onAddedDone: (() -> Unit)?,
            onAddedFailed: (() -> Unit)?
    ) -> (() -> Unit) = { onAddedDone, onAddedFailed ->
        {
            viewModelScope.launch {
                onAddItemToCart(onAddedDone, onAddedFailed)
            }
        }
    }

    val onAddItemToCart: (
        onAddedDone: (() -> Unit)?,
        onAddedFailed: (() -> Unit)?
    ) -> Unit = { onAddedDone, onAddedFailed ->
        viewModelScope.launch {
            val product = uiState.value.product
            val selectedColor = uiState.value.selectedColor
            val selectedSize = uiState.value.selectedSize
            val quantity = uiState.value.quantity

            val correspondingVariant =
                if (selectedColor != null && selectedSize != null)
                    product.variants.find {
                        it.color.id == selectedColor.id && it.size.id == selectedSize.id
                    }
                else null

            if (correspondingVariant != null) {
                val cartItem = CartItem(
                    productId = product.id,
                    variantId = correspondingVariant.id,
                    amount = quantity
                )

                cartItemRepository.addCartItemToCart(cartItem)

                // TODO: Notify user that product is added to cart
                //      Temporary solution
                onAddedDone?.invoke()

            } else {

                onAddedFailed?.invoke()
                // TODO: Notify user to select color and size
            }
        }
    }
}
