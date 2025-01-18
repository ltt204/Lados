package org.nullgroup.lados.data.repositories.interfaces.category

import org.nullgroup.lados.data.models.Category

interface CategoryRepository {
    suspend fun getAllCategoriesFromFireStore(): Result<List<Category>>
    suspend fun getCategoryByIdFromFireStore(id: String): Result<Category?>

    suspend fun getAllSortedAndFilteredCategoriesFromFireStore(
        filterField: String? = null,
        filterValue: Any? = null,
        sortByField: String = "name",
        ascending: Boolean = true
    ): Result<List<Category>>

}