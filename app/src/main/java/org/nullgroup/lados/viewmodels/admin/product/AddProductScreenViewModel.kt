package org.nullgroup.lados.viewmodels.admin.product

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.AddColor
import org.nullgroup.lados.data.models.AddProduct
import org.nullgroup.lados.data.models.AddProductVariant
import org.nullgroup.lados.data.models.AddSize
import org.nullgroup.lados.data.models.UserProfilePicture
import org.nullgroup.lados.data.repositories.interfaces.common.ImageRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.utilities.EXCHANGE_RATE
import org.nullgroup.lados.viewmodels.customer.profile.ProfilePictureUiState
import javax.inject.Inject

val colorOptionsList = listOf(
    AddColor(colorName = mapOf("en" to "Black", "vi" to "Đen"), hexCode = "#000000"),
    AddColor(colorName = mapOf("en" to "White", "vi" to "Trắng"), hexCode = "#FFFFFF"),
    AddColor(colorName = mapOf("en" to "Blue", "vi" to "Xanh dương"), hexCode = "#0000FF"),
    AddColor(colorName = mapOf("en" to "Red", "vi" to "Đỏ"), hexCode = "#FF0000"),
    AddColor(colorName = mapOf("en" to "Gray", "vi" to "Xám"), hexCode = "#808080")
)

val sizeOptionsList = listOf(
    AddSize(sizeName = mapOf("en" to "S", "vi" to "S")),
    AddSize(sizeName = mapOf("en" to "M", "vi" to "M")),
    AddSize(sizeName = mapOf("en" to "L", "vi" to "L")),
    AddSize(sizeName = mapOf("en" to "XL", "vi" to "XL")),
    AddSize(sizeName = mapOf("en" to "XXL", "vi" to "XXL"))
)

fun exchangePrice(price: String, priceOption: String): Map<String, Double> {
    return if (priceOption == "USD") {
        mapOf(
            "en" to price.toDouble(),
            "vi" to price.toDouble() / EXCHANGE_RATE
        )
    } else {
        mapOf(
            "en" to price.toDouble() * EXCHANGE_RATE,
            "vi" to price.toDouble()
        )
    }
}

fun validatePrice(price: String, priceOption: String): Pair<Boolean, String> {
    if (price.isEmpty()) return Pair(false, "Price cannot be empty")

    if (priceOption == "USD") {
        return Pair(price.toDoubleOrNull() != null, "Price must be USD format")
    } else {
        if (price.toIntOrNull() == null) return Pair(false, "Price must be VND format")
    }

    return Pair(true, "")
}

fun validateQuantity(quantity: String): Pair<Boolean, String> {
    if (quantity.isEmpty()) return Pair(false, "Quantity cannot be empty")
    if (quantity.toIntOrNull() == null) return Pair(false, "Quantity must be a number")
    return Pair(true, "")
}

fun validateSaleAmount(saleAmount: String, quantity: String): Pair<Boolean, String> {

    if (saleAmount.isEmpty()) return Pair(false, "Sale amount cannot be empty")
    if (saleAmount.toIntOrNull() == null) return Pair(false, "Sale amount must be a number")
    if (saleAmount.toInt() > quantity.toInt()) return Pair(
        false,
        "Sale amount must be less than quantity"
    )

    return Pair(true, "")
}

fun validateVariant(
    color: String,
    size: String,
    variants: List<AddProductVariant>
): Pair<Boolean, String> {

    if (color.isEmpty() || size.isEmpty()) return Pair(false, "Color and size cannot be empty")

    if (variants.any { it.color.colorName["en"] == color && it.size.sizeName["en"] == size }) {
        return Pair(false, "Variant already exists")
    }

    return Pair(true, "")
}

object VariantRepository {
    private val _variants = mutableListOf<AddProductVariant>()
    val variants: List<AddProductVariant> get() = _variants

    fun addVariant(variant: AddProductVariant) {
        _variants.add(variant)
    }

    fun clearVariants() {
        _variants.clear()
    }
}

@HiltViewModel
class AddProductScreenViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    var productUiState: MutableState<ProductUiState> = mutableStateOf(ProductUiState.Loading)
        private set

    private var _currentProduct: MutableStateFlow<String> = MutableStateFlow("")
    val currentProduct: MutableStateFlow<String> get() = _currentProduct


    init {
        createBlankProduct()
    }


    fun createBlankProduct() {
        viewModelScope.launch {
            val result = productRepository.addProductToFireStoreAndReturnId(AddProduct())
            if (result.isSuccess) {
                _currentProduct.value = result.getOrNull() ?: ""
            }
        }
    }

    fun onAddProduct(product: AddProduct) {
        viewModelScope.launch {
            Log.d(
                "Product",
                "onAddProduct: ${product}"
            )
            productUiState.value = ProductUiState.Loading
            try {
                product.variants.forEach { variant ->
                    variant.images.forEach { image ->
                        Log.d("Test", "onAddProduct: ${image.image}")
                        imageRepository.uploadImage(
                            child = "products",
                            fileName = "${product.id}_${variant.color.colorName["en"]}_${variant.size.sizeName["en"]}",
                            extension = "png",
                            image = image.image ?: byteArrayOf()
                        )

                       val url = imageRepository.getImageUrl(
                            child = "products",
                            fileName = "${product.id}_${variant.color.colorName["en"]}_${variant.size.sizeName["en"]}",
                            fileExtension = "png"
                       )

                        Log.d("Test url", url)
                    }
                }

                // productRepository.addProductToFireStore(product)
            } catch (e: Exception) {
                productUiState.value = ProductUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun onAddVariant(variant: AddProductVariant) {
        viewModelScope.launch {
            VariantRepository.addVariant(variant)
        }
    }

    fun clearVariant(variant: AddProductVariant) {
        viewModelScope.launch {
            VariantRepository.clearVariants()
        }
    }
}


sealed class ProductUiState {
    data object Loading : ProductUiState()
    data class Success(val product: AddProduct) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}

sealed class VariantImageUiState {
    data class Initial(val uri: String) : VariantImageUiState()
    data class Success(val uri: String) : VariantImageUiState()
    data object Loading : VariantImageUiState()
    data class Error(val message: String) : VariantImageUiState()
}