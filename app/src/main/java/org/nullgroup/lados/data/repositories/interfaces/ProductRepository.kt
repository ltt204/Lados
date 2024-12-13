package org.nullgroup.lados.data.repositories.interfaces

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.Product

interface ProductRepository {
    fun getProductsFlow(): Flow<List<Product>>
    fun getProductByIdFlow(id: String): Flow<Product?>

    suspend fun addProductsToFireStore(products: List<Product>): Result<Boolean>
    suspend fun getAllProductsFromFireStore(): Result<List<Product>>
    suspend fun addProductToFireStore(product: Product): Result<Boolean>
    suspend fun getProductByIdFromFireStore(id: String): Result<Product?>
    suspend fun deleteProductByIdFromFireStore(id: String): Result<Boolean>
}