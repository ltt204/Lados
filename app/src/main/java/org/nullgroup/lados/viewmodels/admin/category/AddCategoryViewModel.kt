package org.nullgroup.lados.viewmodels.admin.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.remote.models.CategoryRemoteModel
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.common.ImageRepository
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _addCategoryUiState = MutableStateFlow<AddCategoryUiState>(
        AddCategoryUiState.Error(
            ""
        )
    )
    val addCategoryUiState: StateFlow<AddCategoryUiState> = _addCategoryUiState.asStateFlow()


    fun addCategory(name: Map<String, String>, image: ByteArray) {
        viewModelScope.launch {
            _addCategoryUiState.value = AddCategoryUiState.Loading

            try {
                val createId = categoryRepository.getCategoryId()

                val id = if (createId.isSuccess) {
                    createId.getOrNull()
                } else {
                    null
                }

                if (id != null) {
                    imageRepository.uploadImage(
                        image = image,
                        child = "categories",
                        fileName = "${id}",
                        extension = "jpg"
                    )
                }

                val url = imageRepository.getImageUrl(
                    child = "categories",
                    fileName = "${id}",
                    fileExtension = "jpg"
                )

                val category = CategoryRemoteModel(
                    categoryId = id.toString(),
                    categoryImage = url,
                    categoryName = name
                )

                val result = categoryRepository.updateCategory(id.toString(), category)

                if(result.isSuccess){
                    _addCategoryUiState.value = AddCategoryUiState.Success(category)
                } else {
                    _addCategoryUiState.value =
                        AddCategoryUiState.Error(result.exceptionOrNull().toString())
                }
            } catch (e: Exception){
                _addCategoryUiState.value = AddCategoryUiState.Error(e.message.toString())
            }
        }
    }
}


sealed class AddCategoryUiState {
    data class Success(val categories: CategoryRemoteModel) : AddCategoryUiState()
    data class Error(val message: String) : AddCategoryUiState()
    data object Loading : AddCategoryUiState()
}
