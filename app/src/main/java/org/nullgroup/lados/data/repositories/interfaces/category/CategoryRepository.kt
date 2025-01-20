package org.nullgroup.lados.data.repositories.interfaces.category

import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.remote.models.CategoryRemoteModel

interface CategoryRepository {
    suspend fun getAllCategoriesFromFireStore(): Result<List<Category>>
    suspend fun getCategoryByIdFromFireStore(id: String): Result<Category?>

    suspend fun getAllSortedAndFilteredCategoriesFromFireStore(
        filterField: String? = null,
        filterValue: Any? = null,
        sortByField: String = "name",
        ascending: Boolean = true
    ): Result<List<Category>>

    suspend fun getCategoryId(): Result<String>
    suspend fun addCategory(category: CategoryRemoteModel): Result<Boolean>
    suspend fun deleteCategory(id: String): Result<Boolean>
    suspend fun updateCategory(id: String, category: CategoryRemoteModel): Result<Boolean>
    suspend fun getCategoryRemoteByIdFromFireStore(id: String): Result<CategoryRemoteModel?>


}