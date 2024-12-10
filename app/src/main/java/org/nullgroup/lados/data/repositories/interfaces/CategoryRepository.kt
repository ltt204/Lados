package org.nullgroup.lados.data.repositories.interfaces

import org.nullgroup.lados.data.models.Category

interface CategoryRepository {
    suspend fun getAllCategoriesFromFireStore(): Result<List<Category>>
    suspend fun getCategoryByIdFromFireStore(id: String): Result<Category?>

}