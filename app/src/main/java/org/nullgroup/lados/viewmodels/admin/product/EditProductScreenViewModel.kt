package org.nullgroup.lados.viewmodels.admin.product

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.models.UserProfilePicture
import org.nullgroup.lados.data.remote.models.ProductRemoteModel
import org.nullgroup.lados.data.remote.models.ProductVariantRemoteModel
import org.nullgroup.lados.data.repositories.implementations.product.ProductVariantRepository
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.common.ImageRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import javax.inject.Inject

@HiltViewModel
class EditProductScreenViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val variantRepository: ProductVariantRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    val isInfoChanged = mutableStateOf(false)

    val isVariantPictureChanged = mutableStateOf(false)

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

    private val _categories = MutableStateFlow<List<Category>>(listOf())
    val categories: MutableStateFlow<List<Category>> get() = _categories

    fun loadProduct(productId: String){
        productUiState.value =EditProductUiState.Loading
        viewModelScope.launch {
            try {
                val result = productRepository.getProductRemoteModelByIdFromFireStore(productId)
                if(result.isSuccess){
                    val product = result.getOrNull()
                    if(product != null){
                        productUiState.value = EditProductUiState.Success(product)
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

