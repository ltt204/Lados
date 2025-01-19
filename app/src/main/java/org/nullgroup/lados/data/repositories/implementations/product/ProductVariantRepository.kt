package org.nullgroup.lados.data.repositories.implementations.product

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.remote.models.ProductVariantRemoteModel
import org.nullgroup.lados.data.repositories.interfaces.common.ImageRepository
import org.nullgroup.lados.data.repositories.interfaces.product.IProductVariantRepository

class ProductVariantRepository(
    private val firestore: FirebaseFirestore,
    private val imageRepository: ImageRepository,
) : IProductVariantRepository {
    override suspend fun addVariant(variant: ProductVariantRemoteModel, productId: String) {
        val productDocRef = firestore.collection("products").document(productId)

        val variantData = hashMapOf(
            productId to productId,
            "size" to variant.size,
            "color" to variant.color,
            "quantityInStock" to variant.quantityInStock,
            "originalPrice" to variant.originalPrice,
            "salePrice" to variant.salePrice,
            "images" to variant.images
        )

        productDocRef.collection("variants").add(variantData).await()
    }

    override suspend fun getVariantById(id: String): ProductVariantRemoteModel {
        TODO("Not yet implemented")
    }

    override suspend fun getAllVariants(): List<ProductVariantRemoteModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductVariantId(): Result<String> {
        return try {
            val id = firestore.collection("products").document().id
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteVariantById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearVariants(productId: String) {
        val productDocRef = firestore.collection("products").document(productId)
        val variants = productDocRef.collection("variants").get().await()

        variants.documents.forEach { document ->
            productDocRef.collection("variants").document(document.id).delete().await()
            imageRepository.deleteImage(
                child = "products",
                fileName = document.id,
                extension = "png"
            )
        }
    }
}