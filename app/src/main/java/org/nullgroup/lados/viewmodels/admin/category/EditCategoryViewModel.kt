package org.nullgroup.lados.viewmodels.admin.category

import android.util.Log
import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.models.UserProfilePicture
import org.nullgroup.lados.data.remote.models.CategoryRemoteModel
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.common.ImageRepository
import org.nullgroup.lados.viewmodels.customer.profile.ProfilePictureUiState
import org.nullgroup.lados.viewmodels.customer.profile.ProfilePictureUiState.Loading
import org.nullgroup.lados.viewmodels.customer.profile.UserUiState
import javax.inject.Inject

data class CategoryPicture(
    val image: ByteArray = byteArrayOf(),
    val child: String = "",
    val fileName: String = "",
    val extension: String = ""
)

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    val isInfoChanged = mutableStateOf(false)

    val isCategoryPictureChanged = mutableStateOf(false)

    var categoryUiState: MutableState<EditCategoryUiState> = mutableStateOf(EditCategoryUiState.Loading)
        private set

    private var categoryPicture = mutableStateOf(CategoryPicture())

    var categoryPictureUiState: MutableState<CategoryPictureUiState> =
        mutableStateOf(CategoryPictureUiState.Initial(""))

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _originalParent = MutableStateFlow<Category?>(null)
    val originalParent: StateFlow<Category?> = _originalParent.asStateFlow()


    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            val result = categoryRepository.getAllCategoriesFromFireStore()
            if (result.isSuccess) {
                _categories.value += Category(
                    categoryId = "",
                    categoryName = "No parent",
                    categoryImage = ""
                )
                _categories.value += result.getOrDefault(emptyList())
            } else {
                _categories.value = emptyList()
            }
        }
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            if (categoryUiState.value is EditCategoryUiState.Success) {
                val category = (categoryUiState.value as EditCategoryUiState.Success).category
                categoryUiState.value = EditCategoryUiState.Loading
                try {
                    if (isInfoChanged.value) {
                        try {
                            if (isCategoryPictureChanged.value) {
                                categoryPictureUiState.value = CategoryPictureUiState.Loading
                                try {
                                    imageRepository.deleteImage(
                                        child = "users",
                                        fileName = category.categoryId,
                                        extension = "jpg"
                                    )
                                } catch (e: Exception) {
                                    // Ignore
                                }

                                val firebaseStorageUrl = imageRepository.uploadImage(
                                    categoryPicture.value.image,
                                    categoryPicture.value.child,
                                    categoryPicture.value.fileName,
                                    categoryPicture.value.extension
                                )

                                delay(500)
                                categoryPictureUiState.value =
                                    CategoryPictureUiState.Success(firebaseStorageUrl)
                            } else {
                                categoryPictureUiState.value =
                                    CategoryPictureUiState.Success(category.categoryImage)
                            }
                        } catch (e: Exception) {
                            categoryPictureUiState.value =
                                CategoryPictureUiState.Error(e.message ?: "An error occurred")
                        }

                        category.categoryImage =
                            (categoryPictureUiState.value as CategoryPictureUiState.Success).uri
                        categoryRepository.updateCategory(category.categoryId, category)
                    } else {
                        categoryRepository.updateCategory(category.categoryId, category)
                        categoryPictureUiState.value = CategoryPictureUiState.Success(category.categoryImage)
                        categoryUiState.value = EditCategoryUiState.Success(category)
                    }
                    Log.d("EditCaetgoryViewModel", "Category : $category")
                } catch (e: Exception) {
                    categoryUiState.value = EditCategoryUiState.Error(e.message ?: "An error occurred")
                }
            }
        }
    }

    fun onCategoryPictureChanged(
        uri: String,
        image: ByteArray
    ) {
        viewModelScope.launch {
            if (categoryUiState.value is EditCategoryUiState.Success) {
                val category = (categoryUiState.value as EditCategoryUiState.Success).category
                categoryUiState.value = EditCategoryUiState.Success(category.copy(categoryImage = uri))

                categoryPicture.value = CategoryPicture(
                    image = image,
                    child = "categories",
                    fileName = category.categoryId,
                    extension = "jpg"
                )

                isCategoryPictureChanged.value = true
                isInfoChanged.value = true
            }
        }
    }

    fun onNameChanged(name: Map<String, String>) {
        viewModelScope.launch {
            delay(500)
            if (categoryUiState.value is EditCategoryUiState.Success) {
                isInfoChanged.value = true
                val category = (categoryUiState.value as EditCategoryUiState.Success).category
                categoryUiState.value = EditCategoryUiState.Success(category.copy(categoryName = name))
            }
        }
    }

    fun onParentCategoryChanged(parentId: String){
        viewModelScope.launch {
            delay(500)
            if (categoryUiState.value is EditCategoryUiState.Success) {
                isInfoChanged.value = true
                val category = (categoryUiState.value as EditCategoryUiState.Success).category
                categoryUiState.value = EditCategoryUiState.Success(category.copy(parentCategoryId = parentId))
            }
        }
    }


    fun loadCategory(id: String) {
        categoryUiState.value = EditCategoryUiState.Loading
        viewModelScope.launch {
            try {
                val currentCategory = categoryRepository.getCategoryRemoteByIdFromFireStore(id).getOrNull() ?: CategoryRemoteModel()
                categoryUiState.value = EditCategoryUiState.Success(currentCategory)
                categoryPictureUiState.value = CategoryPictureUiState.Initial(currentCategory.categoryImage)
                Log.d("See parent Id", currentCategory.parentCategoryId)
                Log.d("See parent Id", _categories.value.toString())
                _originalParent.value = _categories.value.find { it.categoryId == currentCategory.parentCategoryId }
                Log.d("EditCategoryViewModel", "Category picture: ${currentCategory.categoryImage}")
            } catch (e: Exception) {
                categoryUiState.value = EditCategoryUiState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class EditCategoryUiState {
    data class Success(val category: CategoryRemoteModel) : EditCategoryUiState()
    data class Error(val message: String) : EditCategoryUiState()
    data object Loading : EditCategoryUiState()
}

sealed class CategoryPictureUiState {
    data class Initial(val uri: String) : CategoryPictureUiState()
    data class Success(val uri: String) : CategoryPictureUiState()
    data object Loading : CategoryPictureUiState()
    data class Error(val message: String) : CategoryPictureUiState()
}