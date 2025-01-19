package org.nullgroup.lados.data.repositories.interfaces.product

import org.nullgroup.lados.data.remote.models.ProductVariantRemoteModel

interface IProductVariantRepository {
    suspend fun addVariant(variant: ProductVariantRemoteModel, productId: String)
    suspend fun getVariantById(id: String): ProductVariantRemoteModel
    suspend fun getAllVariants(): List<ProductVariantRemoteModel>
    suspend fun getProductVariantId(): Result<String>
    suspend fun deleteVariantById(id: String)
    suspend fun clearVariants(productId: String)
}