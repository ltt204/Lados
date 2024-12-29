package org.nullgroup.lados.data.repositories.interfaces.product

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.ProductVariant

interface ProductVariantRepository {
    fun getAllVariantsByProductIdFlow(productId: String): Flow<List<ProductVariant>>
    fun getVariantById(variantId: String): Flow<ProductVariant>

    suspend fun addProductVariantToFireStore(productVariant: ProductVariant): Result<Boolean>
    suspend fun getProductVariantByIdFromFireStore(id: String): Result<ProductVariant?>
    suspend fun deleteProductVariantByIdFromFireStore(id: String): Result<Boolean>
    suspend fun updateProductVariantInFireStore(productVariant: ProductVariant): Result<Boolean>
}