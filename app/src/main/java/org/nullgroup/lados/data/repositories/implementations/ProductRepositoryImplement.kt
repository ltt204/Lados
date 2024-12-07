package org.nullgroup.lados.data.repositories.implementations

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Image
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.models.UserEngagement
import org.nullgroup.lados.data.repositories.interfaces.ProductRepository

class ProductRepositoryImplement (
    private val firestore: FirebaseFirestore
) : ProductRepository {

    override suspend fun addProductsToFireStore(products: List<Product>): Result<Boolean> {
        return try {
            val batch = firestore.batch()
            for (product in products) {
                val productDocRef = firestore.collection("products").document()

                val productData = hashMapOf(
                    "categoryId" to product.categoryId,
                    "name" to product.name,
                    "description" to product.description,
                    "createdAt" to product.createdAt
                )
                batch.set(productDocRef, productData)

                for (variant in product.variants) {
                    val variantDocRef = productDocRef.collection("variants").document()
                    val variantData = hashMapOf(
                        "productId" to productDocRef.id,
                        "size" to hashMapOf(
                            "id" to variant.size.id,
                            "sizeName" to variant.size.sizeName,
                            "sortOrder" to variant.size.sortOrder
                        ),
                        "color" to hashMapOf(
                            "id" to variant.color.id,
                            "colorName" to variant.color.colorName,
                            "hexCode" to variant.color.hexCode
                        ),
                        "quantityInStock" to variant.quantityInStock,
                        "originalPrice" to variant.originalPrice,
                        "salePrice" to variant.salePrice
                    )
                    batch.set(variantDocRef, variantData)

                    for (image in variant.images) {
                        val imageDocRef = variantDocRef.collection("images").document()
                        val imageData = hashMapOf(
                            "productVariantId" to variantDocRef.id,
                            "link" to image.link,
                            "fileName" to image.fileName
                        )
                        batch.set(imageDocRef, imageData)
                    }
                }

                for (engagement in product.engagements) {
                    val engagementDocRef = productDocRef.collection("engagements").document()
                    val engagementData = hashMapOf(
                        "userId" to engagement.userId,
                        "productId" to productDocRef.id,
                        "ratings" to engagement.ratings,
                        "reviews" to engagement.reviews,
                        "createdAt" to engagement.createdAt
                    )
                    batch.set(engagementDocRef, engagementData)
                }
            }

            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllProductsFromFireStore(): Result<List<Product>> {
        return try {
            val productList = firestore.collection("products")
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    val product = document.toObject(Product::class.java)
                    product?.let {

                        val variants = firestore.collection("products")
                            .document(it.id)
                            .collection("variants")
                            .get()
                            .await()
                            .documents
                            .mapNotNull { variantDoc ->
                                val variant = variantDoc.toObject(ProductVariant::class.java)
                                variant?.let { v ->

                                    val images = firestore.collection("products")
                                        .document(it.id)
                                        .collection("variants")
                                        .document(v.id)
                                        .collection("images")
                                        .get()
                                        .await()
                                        .documents
                                        .mapNotNull { imageDoc ->
                                            imageDoc.toObject(Image::class.java)
                                        }
                                    v.images = images
                                }
                                variant
                            }
                        it.variants = variants


                        val engagements = firestore.collection("products")
                            .document(it.id)
                            .collection("engagements")
                            .get()
                            .await()
                            .documents
                            .mapNotNull { engagementDoc ->
                                engagementDoc.toObject(UserEngagement::class.java)
                            }
                        it.engagements = engagements

                        it
                    }
                }

            Result.success(productList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addProductToFireStore(product: Product): Result<Boolean> {
        return try {
            val productDocRef = firestore.collection("products").document()

            val productData = hashMapOf(
                "categoryId" to product.categoryId,
                "name" to product.name,
                "description" to product.description,
                "createdAt" to product.createdAt
            )
            productDocRef.set(productData).await()

            for (variant in product.variants) {

                val variantDocRef = productDocRef.collection("variants").document()

                val variantData = hashMapOf(
                    "productId" to productDocRef.id,
                    "size" to hashMapOf(
                        "id" to variant.size.id,
                        "sizeName" to variant.size.sizeName,
                        "sortOrder" to variant.size.sortOrder
                    ),
                    "color" to hashMapOf(
                        "id" to variant.color.id,
                        "colorName" to variant.color.colorName,
                        "hexCode" to variant.color.hexCode
                    ),
                    "quantityInStock" to variant.quantityInStock,
                    "originalPrice" to variant.originalPrice,
                    "salePrice" to variant.salePrice
                )
                variantDocRef.set(variantData).await()

                for (image in variant.images) {
                    val imageDocRef = variantDocRef.collection("images").document()
                    val imageData = hashMapOf(
                        "productVariantId" to variantDocRef.id,
                        "link" to image.link,
                        "fileName" to image.fileName
                    )
                    imageDocRef.set(imageData).await()
                }
            }


            for (engagement in product.engagements) {
                val engagementDocRef = productDocRef.collection("engagements").document()
                val engagementData = hashMapOf(
                    "userId" to engagement.userId,
                    "productId" to productDocRef.id,
                    "ratings" to engagement.ratings,
                    "reviews" to engagement.reviews,
                    "createdAt" to engagement.createdAt
                )
                engagementDocRef.set(engagementData).await()
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductByIdFromFireStore(id: String): Result<Product?> {
        return try {
            val productDoc = firestore.collection("products")
                .document(id)
                .get()
                .await()

            val product = productDoc.toObject(Product::class.java)
            product?.let {

                val variants = firestore.collection("products")
                    .document(it.id)
                    .collection("variants")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { variantDoc ->
                        val variant = variantDoc.toObject(ProductVariant::class.java)
                        variant?.let { v ->
                            val images = firestore.collection("products")
                                .document(it.id)
                                .collection("variants")
                                .document(v.id)
                                .collection("images")
                                .get()
                                .await()
                                .documents
                                .mapNotNull { imageDoc ->
                                    imageDoc.toObject(Image::class.java)
                                }
                            v.images = images
                        }
                        variant
                    }
                it.variants = variants

                val engagements = firestore.collection("products")
                    .document(it.id)
                    .collection("engagements")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { engagementDoc ->
                        engagementDoc.toObject(UserEngagement::class.java)
                    }
                it.engagements = engagements
            }

            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProductByIdFromFireStore(id: String): Result<Boolean> {
        return try {
            val variants = firestore.collection("products")
                .document(id)
                .collection("variants")
                .get()
                .await()

            val batch = firestore.batch()

            for (variantDoc in variants.documents) {
                val variantId = variantDoc.id
                val variantRef = firestore.collection("products")
                    .document(id)
                    .collection("variants")
                    .document(variantId)
                batch.delete(variantRef)

                val images = firestore.collection("products")
                    .document(id)
                    .collection("variants")
                    .document(variantId)
                    .collection("images")
                    .get()
                    .await()

                for (imageDoc in images.documents) {
                    val imageRef = firestore.collection("products")
                        .document(id)
                        .collection("variants")
                        .document(variantId)
                        .collection("images")
                        .document(imageDoc.id)
                    batch.delete(imageRef)
                }
            }

            val engagements = firestore.collection("products")
                .document(id)
                .collection("engagements")
                .get()
                .await()

            for (engagementDoc in engagements.documents) {
                val engagementRef = firestore.collection("products")
                    .document(id)
                    .collection("engagements")
                    .document(engagementDoc.id)
                batch.delete(engagementRef)
            }

            val productRef = firestore.collection("products").document(id)
            batch.delete(productRef)

            batch.commit().await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

