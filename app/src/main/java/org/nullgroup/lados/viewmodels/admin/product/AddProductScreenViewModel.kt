package org.nullgroup.lados.viewmodels.admin.product

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import org.nullgroup.lados.data.models.AddProduct
import org.nullgroup.lados.data.models.AddProductVariant
import org.nullgroup.lados.data.models.Image
import org.nullgroup.lados.data.remote.models.ColorRemoteModel
import org.nullgroup.lados.data.remote.models.ProductRemoteModel
import org.nullgroup.lados.data.remote.models.ProductVariantRemoteModel
import org.nullgroup.lados.data.remote.models.SizeRemoteModel
import org.nullgroup.lados.data.repositories.implementations.product.ProductVariantRepository
import org.nullgroup.lados.data.repositories.interfaces.common.ImageRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.utilities.EXCHANGE_RATE
import javax.inject.Inject

val colorOptionsList = listOf(
    ColorRemoteModel(colorName = mapOf("en" to "Black", "vi" to "Đen"), hexCode = "#000000"),
    ColorRemoteModel(colorName = mapOf("en" to "White", "vi" to "Trắng"), hexCode = "#FFFFFF"),
    ColorRemoteModel(colorName = mapOf("en" to "Blue", "vi" to "Xanh dương"), hexCode = "#0000FF"),
    ColorRemoteModel(colorName = mapOf("en" to "Red", "vi" to "Đỏ"), hexCode = "#FF0000"),
    ColorRemoteModel(colorName = mapOf("en" to "Gray", "vi" to "Xám"), hexCode = "#808080")
)

val sizeOptionsList = listOf(
    SizeRemoteModel(sizeName = mapOf("en" to "S", "vi" to "S")),
    SizeRemoteModel(sizeName = mapOf("en" to "M", "vi" to "M")),
    SizeRemoteModel(sizeName = mapOf("en" to "L", "vi" to "L")),
    SizeRemoteModel(sizeName = mapOf("en" to "XL", "vi" to "XL")),
    SizeRemoteModel(sizeName = mapOf("en" to "XXL", "vi" to "XXL"))
)

fun exchangePrice(price: String, priceOption: String): Map<String, Double> {
    return if (priceOption == "USD") {
        mapOf(
            "en" to price.toDouble(),
            "vi" to price.toDouble() * EXCHANGE_RATE
        )
    } else {
        mapOf(
            "en" to price.toDouble(),
            "vi" to price.toDouble() * EXCHANGE_RATE
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
    variants: List<ProductVariantRemoteModel>
): Pair<Boolean, String> {

    if (color.isEmpty() || size.isEmpty()) return Pair(false, "Color and size cannot be empty")

    if (variants.any { it.color.colorName["en"] == color && it.size.sizeName["en"] == size }) {
        return Pair(false, "Variant already exists")
    }

    return Pair(true, "")
}

fun validateDescription(description: Map<String, String>): Pair<Boolean, String> {
    if (description.isEmpty()) return Pair(false, "Description cannot be empty")
    return Pair(true, "")
}

fun validateName(name: Map<String, String>): Pair<Boolean, String> {
    if (name.isEmpty()) return Pair(false, "Name cannot be empty")
    return Pair(true, "")
}

fun validateVariant(variants: List<ProductVariantRemoteModel>): Pair<Boolean, String>{
    if (variants.isEmpty()) return Pair(false, "You must add at least one variant")
    return Pair(true, "")
}

@HiltViewModel
class AddProductScreenViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val variantRepository: ProductVariantRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    var productUiState: MutableState<ProductUiState> = mutableStateOf(ProductUiState.Loading)
        private set

    var uploadImageState = MutableStateFlow<VariantImageUiState>(VariantImageUiState.Initial(""))
        private set
    private var _currentProductId: MutableStateFlow<String> = MutableStateFlow("")
    val currentProductId: MutableStateFlow<String> get() = _currentProductId

    private var _productZombie: MutableStateFlow<ProductRemoteModel> =
        MutableStateFlow(ProductRemoteModel())
    val productZombie: MutableStateFlow<ProductRemoteModel> get() = _productZombie

    var productVariants = MutableStateFlow<List<ProductVariantRemoteModel>>(
        listOf()
    )
        private set

    init {
        createBlankProduct()
    }

    fun clearProductZombie(){
        viewModelScope.launch {
            _productZombie.value = ProductRemoteModel()
        }
    }

    fun clearProductVariants(){
        viewModelScope.launch {
            productVariants.value = listOf()
        }
    }


    fun createBlankProduct() {
        viewModelScope.launch {
            val result = productRepository.getProductId()
            if (result.isSuccess) {
                _currentProductId.value = result.getOrNull() ?: ""
            }
        }
    }

    fun onAddProductButtonClick() {
        viewModelScope.launch {
            productUiState.value = ProductUiState.Loading
            try {
                Log.d(
                    "Product",
                    "onAddProduct: Variants ${productVariants.value}"
                )
                _productZombie.value.variants = productVariants.value
                Log.d(
                    "Product",
                    "onAddProduct: Product ${_productZombie.value}"
                )
                val result = productRepository.addProductToFireStore(_productZombie.value)
                if (result.isSuccess) {
                    productUiState.value = ProductUiState.Success(AddProduct())
                } else {
                    productUiState.value = ProductUiState.Error(result.exceptionOrNull()?.message ?: "An error occurred")
                }
            } catch (e: Exception) {
                productUiState.value = ProductUiState.Error(e.message ?: "An error occurred")
                Log.d("Product", "onAddProduct: ${e.message}")
            }
        }
    }

    fun onAddVariant(variant: ProductVariantRemoteModel, withImageByteArray: ByteArray) {
        viewModelScope.launch {
            Log.d("AddProductScreenViewModel", "variant: $variant")
            val productVariantId = variantRepository.getProductVariantId().getOrNull() ?: ""
            Log.d("AddProductScreenViewModel", "productVariantId: $productVariantId")

            uploadImageState.value = VariantImageUiState.Loading
            val imageUrl = imageRepository.uploadImage(
                child = "products",
                fileName = productVariantId,
                extension = "png",
                image = withImageByteArray
            ).getOrNull() ?: throw Exception("Image upload failed")
            Log.d("AddProductScreenViewModel", "imageUrl: $imageUrl")
            variant.images = listOf(
                Image(
                    productVariantId = productVariantId,
                    link = imageUrl,
                    fileName = productVariantId,
                )
            )
//            _productZombie.value.variants += variant
            productVariants.value += variant
            uploadImageState.value = VariantImageUiState.Success(imageUrl)
            Log.d("AddProductScreenViewModel", "productVariants: ${_productZombie.value.variants}")
        }
    }

    fun clearVariant(variant: AddProductVariant) {
        viewModelScope.launch {
            variantRepository.clearVariants(_currentProductId.value)
        }
    }

    fun onAddProductZombie(product: ProductRemoteModel) {
        viewModelScope.launch {
            _productZombie.value = product
        }
    }

    fun onDescriptionChanged(description: Map<String, String>) {
        viewModelScope.launch {
            delay(500)
            _productZombie.value = _productZombie.value.copy(description = description)
        }
    }

    fun onNameChanged(name: Map<String, String>) {
        viewModelScope.launch {
            delay(500)
            _productZombie.value = _productZombie.value.copy(name = name)
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