package org.nullgroup.lados.viewmodels.admin.product

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.AddProduct
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.models.Image
import org.nullgroup.lados.data.models.UserProfilePicture
import org.nullgroup.lados.data.remote.models.ProductRemoteModel
import org.nullgroup.lados.data.remote.models.ProductVariantRemoteModel
import org.nullgroup.lados.data.repositories.implementations.product.ProductVariantRepository
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.common.ImageRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.screens.Screen
import javax.inject.Inject


fun validateEditVariant(
    color: String,
    size: String,
    variants: List<ProductVariantRemoteModel>
): Pair<Boolean, String> {

    if (color.isEmpty() || size.isEmpty()) return Pair(false, "Color and size cannot be empty")

    return Pair(true, "")
}

@HiltViewModel
class EditProductScreenViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val variantRepository: ProductVariantRepository,
    private val imageRepository: ImageRepository,
    private  val saveStateHandle: SavedStateHandle
) : ViewModel() {


    val isInfoChanged = mutableStateOf(false)

    var isVariantPictureChanged = mutableStateOf(false)

    private var isFirstTimeLoadData = false

    var productUiState: MutableState<EditProductUiState> = mutableStateOf(EditProductUiState.Loading)
        private set

    private var _productZombie: MutableStateFlow<ProductRemoteModel> =
        MutableStateFlow(ProductRemoteModel())
    val productZombie: MutableStateFlow<ProductRemoteModel> get() = _productZombie

    private var variantPicture = mutableStateOf(UserProfilePicture())

    var uploadImageState = MutableStateFlow<VariantImageUiState>(VariantImageUiState.Initial(""))
        private set

    var productVariants = MutableStateFlow<List<ProductVariantRemoteModel>>(
        listOf()
    )
        private set

    // Save image of product variant to delete
    var imageListDelete = arrayListOf<String>()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: MutableStateFlow<Boolean> get() = _updateSuccess

    private val _categories = MutableStateFlow<List<Category>>(listOf())
    val categories: MutableStateFlow<List<Category>> get() = _categories

    fun loadProduct(productId: String){
        productUiState.value = EditProductUiState.Loading
        viewModelScope.launch {
            try {
                val result = productRepository.getProductRemoteModelByIdFromFireStore(productId)
                if(result.isSuccess){
                    val product = result.getOrNull()
                    if(product != null){
                        productUiState.value = EditProductUiState.Success(product)
                        if(!isFirstTimeLoadData){
                            isFirstTimeLoadData = true
                            _productZombie.value = product
                            productVariants.value = product.variants
                        }
                    } else {
                        productUiState.value = EditProductUiState.Error("Product not found")
                    }
                } else {
                    productUiState.value =
                        EditProductUiState.Error(result.exceptionOrNull()?.message.toString())
                }
            } catch (e: Exception){
                productUiState.value = EditProductUiState.Error(e.message.toString())
            }
        }
    }

    fun onUpdateProductButtonClick() {
        viewModelScope.launch {
            productUiState.value = EditProductUiState.Loading
            try {
                _productZombie.value.variants = productVariants.value

                Log.d("Product After Update", "onAddProduct: Product ${_productZombie.value}")

                val result = productRepository.updateProductInFireStore(_productZombie.value)

                if(imageListDelete.isNotEmpty()){
                    imageListDelete.forEach{
                        imageRepository.deleteImage(
                            child = "products",
                            fileName = it,
                            extension = "png",
                        )
                    }
                }
                imageListDelete.clear()

                if (result.isSuccess) {
                    productUiState.value = EditProductUiState.Success(ProductRemoteModel())
                    _updateSuccess.value = true
                } else {
                    productUiState.value = EditProductUiState.Error(result.exceptionOrNull()?.message ?: "An error occurred")
                    _updateSuccess.value = false
                }

            } catch (e: Exception) {
                productUiState.value = EditProductUiState.Error(e.message ?: "An error occurred")
                Log.d("Product", "onUpdateProduct: ${e.message}")
            }
        }
    }

    fun handleUpdateSuccess(){
        viewModelScope.launch {
            _productZombie.value = ProductRemoteModel()
            productVariants.value = listOf()
            isFirstTimeLoadData = false
            _updateSuccess.value = false
            productUiState.value = EditProductUiState.Loading
        }
    }

    fun onUpdateVariant(variant: ProductVariantRemoteModel, withImageByteArray: ByteArray) {
        viewModelScope.launch {

            uploadImageState.value = VariantImageUiState.Loading
            val imageUrl = imageRepository.uploadImage(
                child = "products",
                fileName = variant.id,
                extension = "png",
                image = withImageByteArray
            ).getOrNull() ?: throw Exception("Image upload failed")

            variant.images = listOf(
                Image(
                    productVariantId = variant.id,
                    link = imageUrl,
                    fileName = variant.id,
                )
            )
            productVariants.value = productVariants.value.map { it ->
                if (it.id == variant.id) {
                    variant
                } else {
                    it
                }
            }
            _productZombie.value = _productZombie.value.copy(variants = productVariants.value)
            uploadImageState.value = VariantImageUiState.Success(imageUrl)
        }
    }

    fun onDeleteVariant(variantId: String){
        viewModelScope.launch {
            imageListDelete.add(variantId)
            productVariants.value = productVariants.value.filter { it.id != variantId }

            _productZombie.value = _productZombie.value.copy(variants = productVariants.value)

            uploadImageState.value = VariantImageUiState.Success("")

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

            productVariants.value += variant
            _productZombie.value = _productZombie.value.copy(variants = productVariants.value)
            uploadImageState.value = VariantImageUiState.Success(imageUrl)
            Log.d("AddProductScreenViewModel", "productVariants: ${_productZombie.value.variants}")
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            val result = categoryRepository.getAllCategoriesFromFireStore()
            if (result.isSuccess) {
                _categories.value = result.getOrNull() ?: listOf()
            } else {
                Log.d("AddProductScreenViewModel", "getCategories: ${result.exceptionOrNull()}")
            }
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

    fun onCategoryChanged(categoryId: String) {
        viewModelScope.launch {
            delay(500)
            _productZombie.value = _productZombie.value.copy(categoryId = categoryId)
        }
    }
}

sealed class EditProductUiState{
    data object Loading : EditProductUiState()
    data class Success(val product: ProductRemoteModel) : EditProductUiState()
    data class Error(val message: String) : EditProductUiState()
}

