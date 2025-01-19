package org.nullgroup.lados.data.repositories.implementations.product

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Image
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductNameAndCategory
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.models.UserEngagement
import org.nullgroup.lados.data.remote.models.ProductRemoteModel
import org.nullgroup.lados.data.remote.models.ProductVariantRemoteModel
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.utilities.toLocalProduct

class ProductRepositoryImplement(
    private val firestore: FirebaseFirestore
) : ProductRepository {
    override fun getProductsFlow(): Flow<List<Product>> = callbackFlow {
        val subscription = firestore.collection("products")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    throw error
                }

                val products = value?.documents?.mapNotNull { document ->
                    val remoteProduct = document.toObject(ProductRemoteModel::class.java)
                    remoteProduct?.toLocalProduct()
//                    document.toObject(Product::class.java)
                } ?: emptyList()

                trySend(products).isSuccess
            }

        awaitClose { subscription.remove() }
    }

    override fun getProductsWithRangeOfIdsFlow(ids: List<String>): Flow<List<Product>> =
        callbackFlow {
            firestore.collection("products")
                .whereIn("id", ids)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        throw error
                    }

                    val products = value?.documents?.mapNotNull { document ->
                        val remoteProduct = document.toObject(ProductRemoteModel::class.java)
                        remoteProduct?.toLocalProduct()
                    } ?: emptyList()

                    products.forEach { product ->
                        getProductVariantsByProductId(product.id).addOnSuccessListener {
                            val variants = it.documents.mapNotNull { variantDoc ->
                                variantDoc.toObject(ProductVariant::class.java)
                            }
                            product.variants = variants
                        }
                    }

                    trySend(products).isSuccess
                }
        }

    override fun getProductByIdFlow(id: String): Flow<Product?> = callbackFlow {
        val productRef = firestore.collection("products")
            .document(id)
        val variantRef = firestore.collection("products")
            .document(id)
            .collection("variants")

        val product = productRef.get().await().toObject(ProductRemoteModel::class.java)

        Log.d("ProductRepositoryImplement", "product: ${product?.variants}")

        val variants = variantRef.get().await().documents.mapNotNull { variantDoc ->
            variantDoc.toObject(ProductVariantRemoteModel::class.java)
        }
        for (variant in variants) {
            val variantImagesRef = firestore.collection("products")
                .document(id)
                .collection("variants")
                .document(variant.id)
                .collection("images")

            val images = variantImagesRef.get().await().documents.mapNotNull { imageDoc ->
                imageDoc.toObject(Image::class.java)
            }
            variant.images = images
        }

        product?.variants = variants

        trySend(product?.toLocalProduct()).isSuccess

        awaitClose { }
    }

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
                    val product = document.toObject(ProductRemoteModel::class.java)
                    product?.let {
                        val variants = firestore.collection("products")
                            .document(it.id)
                            .collection("variants")
                            .get()
                            .await()
                            .documents
                            .mapNotNull { variantDoc ->
                                val variant = variantDoc.toObject(ProductVariantRemoteModel::class.java)
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

                        it.toLocalProduct()
                    }
                }

            Result.success(productList)
        } catch (e: Exception) {
            Log.d("ProductRepositoryImplement", "getAllProductsFromFireStore: ${e.message}")
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

            val product = productDoc.toObject(ProductRemoteModel::class.java)

            Log.d("ProductRepositoryImplement", "product: $product")

            product?.let {
                val variants = firestore.collection("products")
                    .document(it.id)
                    .collection("variants")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { variantDoc ->
                        val variant = variantDoc.toObject(ProductVariantRemoteModel::class.java)
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

            Result.success(product?.toLocalProduct())
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

    private fun getProductVariantsByProductId(productId: String): Task<QuerySnapshot> {
        val variants = firestore.collection("products")
            .document(productId)
            .collection("variants")
            .get()
            .addOnSuccessListener {
                val variants = it.documents.mapNotNull { variantDoc ->
                    variantDoc.toObject(ProductVariant::class.java)
                }

                for (variant in variants) {
                    variant.let { v ->
                        firestore.collection("products")
                            .document(productId)
                            .collection("variants")
                            .document(v.id)
                            .collection("images")
                            .get()
                            .addOnSuccessListener { img ->
                                val images = img.documents.mapNotNull { imageDoc ->
                                    imageDoc.toObject(Image::class.java)
                                }
                                v.images = images
                            }
                    }
                }
            }

        return variants
    }

    override suspend fun getAllProductsWithNameAndCategoryFromFireStore(): Result<List<Product>> {
        return try {
            val productList = firestore.collection("products")
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    val product = document.toObject(ProductRemoteModel::class.java)
                    product?.let {

                        it.toLocalProduct()
                    }
                }

            Result.success(productList)
        } catch (e: Exception) {
            Log.d("ProductRepositoryImplement", "getAllProductsFromFireStore: ${e.message}")
            Result.failure(e)
        }
    }
}

