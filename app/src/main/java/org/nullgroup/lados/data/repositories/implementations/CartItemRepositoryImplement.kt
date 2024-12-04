package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Cart
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository

class CartItemRepositoryImplement(
    private val firestore: FirebaseFirestore,
): CartItemRepository {
//    override suspend fun getOrInitializeCart(userId: String): Result<Cart> {
//        val documentRef = firestore.collection(Cart.COLLECTION_PATH).document(userId)
//
//        return try {
//            var document = documentRef.get().await()
//            if (document.exists()) {
//                Log.d("CartItemRepositoryImplement", "Cart already exists")
//                Result.success(Cart.fromMap(document.data!!))
//            } else {
//                val cart = Cart(customerId = userId)
//
//                firestore.runTransaction { transaction ->
//                    transaction.set(documentRef, cart.toMap())
//                }.await()
//                Result.success(cart)
//            }
//        } catch (e: Exception) {
//            Log.e("CartItemRepositoryImplement", "Error initializing cart: ", e)
//            Result.failure(e)
//        }
//
//    }



//    override suspend fun getCartItems(cartId: String): Result<List<CartItem>> {
//        @Suppress("UNCHECKED_CAST")
//        return try {
//            val querySnapshot = firestore.collection(Cart.COLLECTION_PATH)
//                .document(cartId)
//                .get()
//                .await()
//            if (querySnapshot.exists()) {
//                val cartItems = querySnapshot.get("items") as? Map<String, Map<String, Any>>
//                    ?: mapOf()
//                Result.success(cartItems.values.map {
//                    CartItem.fromMap(it)
//                })
//            } else {
//                Result.failure(Exception("Cart not found"))
//            }
//        } catch (e: Exception) {
//            Log.e("CartItemRepositoryImplement", "Error getting cart items: ", e)
//            Result.failure(e)
//        }
//    }

    override suspend fun getOrInitializeCart(customerId: String): Result<Cart> {
        return try {
            val cartRef = firestore.collection(Cart.COLLECTION_NAME).document(customerId)
            val cart = cartRef.get().await()
            if (cart.exists()) {
                Result.success(cart.toObject(Cart::class.java)!!)
            } else {
                val newCart = Cart(customerId = customerId)
                cartRef.set(newCart).await()
                Result.success(newCart)
            }
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error getting cart: ", e)
            Result.failure(e)
        }
    }

    override fun getCartItemsAsFlow(customerId: String): Flow<List<CartItem>> = callbackFlow {
        val listener = firestore.collection(Cart.COLLECTION_NAME)
            .document(customerId)
            .collection(CartItem.COLLECTION_NAME)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val cartItems = snapshot.documents.mapNotNull {
                        it.toObject(CartItem::class.java)
                    }
                    trySend(cartItems)
                }
            }
        awaitClose {
            listener.remove()
        }

    }

    /**
     * Add a cart item to the cart.
     * If the item has "already existed", remove it and add the new one
     **/
    override suspend fun addCartItemToCart(cartId: String, cartItem: CartItem): Result<Boolean> {
        return try {
            val cartRef = firestore.collection(Cart.COLLECTION_NAME).document(cartId)
            val cart = cartRef.get().await()
            if (!cart.exists()) {
                throw Exception("Cart not found")
            }
            val cartCollectionRef = cartRef.collection(CartItem.COLLECTION_NAME)
            val existingItemRef = cartCollectionRef
                .whereEqualTo("productId", cartItem.productId)
                .whereEqualTo("variantId", cartItem.variantId)
                .get().await()
            if (existingItemRef.documents.isNotEmpty()) {
                cartCollectionRef
                    .document(existingItemRef.first().id)
                    .set(cartItem)
                    .await()
            } else {
                cartCollectionRef
                    .add(cartItem)
                    .await()
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error adding cart item: ", e)
            Result.failure(e)
        }
    }

    override suspend fun removeCartItemsFromCart(
        cartId: String,
        cartItemIds: List<String>
    ): Result<Boolean> {
        return try {
            val cartRef = firestore.collection(Cart.COLLECTION_NAME).document(cartId)
            val batch = firestore.batch()
            cartItemIds.forEach { itemId ->
                batch.delete(cartRef.collection(CartItem.COLLECTION_NAME).document(itemId))
            }
            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error removing cart items: ", e)
            Result.failure(e)
        }
    }

    override suspend fun updateCartItemsAmount(
        cartId: String,
        adjustmentInfo: List<Pair<String, Int>>
    ): Result<Boolean> {
        if (adjustmentInfo.isEmpty()) {
            return Result.success(true)
        }
        return try {
            val cartRef = firestore.collection(Cart.COLLECTION_NAME).document(cartId)
            val batch = firestore.batch()
            adjustmentInfo.forEach { (itemId, amount) ->
                if (amount <= 0) {
                    batch.delete(
                        cartRef.collection(CartItem.COLLECTION_NAME).document(itemId)
                    )
                } else {
                    batch.update(
                        cartRef.collection(CartItem.COLLECTION_NAME).document(itemId),
                        "amount",
                        amount
                    )
                }
            }
            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error updating cart items amount: ", e)
            Result.failure(e)
        }
    }

//    override suspend fun getProductAndVariant(
//        productId: String,
//        variantId: String
//    ): Result<Pair<Product?, ProductVariant?>> {
//        return try {
//            val productRef = firestore.collection("products").document(productId)
//            val product = productRef.get().await().toObject(Product::class.java)
//            if (product != null) {
//                val variantRef = productRef
//                    .collection("variants").document(variantId)
//                val variant = variantRef.get().await().toObject(ProductVariant::class.java)
//                if (variant != null) {
//                    val image = variantRef
//                        .collection("images")
//                        .get().await()
//                        .firstNotNullOf { it.toObject(Image::class.java) }
//                    variant.images = listOf(image)
//                }
//                Result.success(product to variant)
//            }
//            Result.success(null to null)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    /**
    * Add a cart item to the cart.
    * If the item has "already existed", remove it and add the new one
    **/
//    override suspend fun addCartItemToCart(
//        cartId: String,
//        cartItem: CartItem
//    ): Result<Boolean> {
//
//        @Suppress("UNCHECKED_CAST")
//        return try {
//            val cartDocRef = firestore.collection(Cart.COLLECTION_PATH).document(cartId)
//            firestore.runTransaction { transaction ->
//                val snapshot = transaction.get(cartDocRef)
//                if (!snapshot.exists()) {
//                    throw Exception("Cart not found")
//                }
//                val currentItems = snapshot.get("items") as? Map<String, Map<String, Any>> ?: mapOf()
//                val existingItemKey = currentItems
//                    .filter {
//                        it.value["productId"] == cartItem.productId &&
//                        it.value["variantId"] == cartItem.variantId
//                    }
//                    .map { it.key }
//                    .firstOrNull()
//
//                if (existingItemKey != null) {
//                    transaction.update(cartDocRef, mapOf(
//                        "items.$existingItemKey.amount" to cartItem.amount,
//                        "items.$existingItemKey.addedAt" to cartItem.addedAt,
//                        "updatedAt" to System.currentTimeMillis()
//                    ))
//                } else {
//                    transaction.update(cartDocRef, mapOf(
//                        "items.${cartItem.id}" to cartItem.toMap(),
//                        "updatedAt" to System.currentTimeMillis()
//                    ))
//                }
//
////                transaction.update(cartDocRef, mapOf(
////                    "items.${cartItem.id}" to cartItem.toMap(),
////                    "updatedAt" to System.currentTimeMillis()
////                ))
//            }.await()
//            Result.success(true)
//        } catch (e: Exception) {
//            Log.e("CartItemRepositoryImplement", "Error adding cart item: ", e)
//            Result.failure(e)
//        }
//    }

//    override suspend fun removeCartItemFromCart(
//        cartId: String,
//        cartItemId: String
//    ): Result<Boolean> {
//
//        @Suppress("UNCHECKED_CAST")
//        return try {
//            val docRef = firestore.collection(Cart.COLLECTION_PATH)
//                .document(cartId)
//            firestore.runTransaction { transaction ->
//                val snapshot = transaction.get(docRef)
//                if (!snapshot.exists()) {
//                    throw Exception("Cart not found")
//                }
//                val currentItems = snapshot.get("items") as? Map<String, Map<String, Any>>
//                    ?: mapOf()
//                if (currentItems.containsKey(cartItemId)) {
//                    transaction.update(docRef, mapOf(
//                        "items.${cartItemId}" to FieldValue.delete(),
//                        "updatedAt" to System.currentTimeMillis()
//                    ))
//                    return@runTransaction
//                } else {
//                    throw Exception("Item not found while removing")
//                }
//
//            }.await()
//            Result.success(true)
//        } catch (e: Exception) {
//            Log.e("CartItemRepositoryImplement", "Error removing cart item: ", e)
//            Result.failure(e)
//        }
//    }

//    override suspend fun removeMultipleCartItemsFromCart(
//        cartId: String,
//        cartItemIds: List<String>
//    ): Result<Boolean> {
//        return try {
//            val cartRef = firestore.collection(Cart.COLLECTION_PATH).document(cartId)
//            val batch = firestore.batch()
//
//            // Remove each cart item from the cart document
//            cartItemIds.forEach { itemId ->
//                batch.update(cartRef, itemId, FieldValue.delete())
//            }
//
//            // Update the updatedAt field
//            batch.update(cartRef, "updatedAt", System.currentTimeMillis())
//
//            // Commit the batch write
//            batch.commit().await()
//            Result.success(true)
//        } catch (e: Exception) {
//            Log.e("CartItemRepository", "Error removing cart items: $e")
//            Result.failure(e)
//        }
//    }

//    /**
//     * If amount is less than 1, remove the item from the cart
//     *  */
//    override suspend fun updateCartItemAmount(
//        cartId: String,
//        cartItemId: String,
//        amount: Int
//    ): Result<Boolean> {
//        @Suppress("UNCHECKED_CAST")
//        return try {
//            val docRef = firestore.collection(Cart.COLLECTION_PATH)
//                .document(cartId)
//            firestore.runTransaction { transaction ->
//                val snapshot = transaction.get(docRef)
//                if (!snapshot.exists()) {
//                    throw Exception("Cart not found")
//                }
//                val currentItems = snapshot.get("items") as? Map<String, Map<String, Any>>
//                    ?: mapOf()
//
//                if (!currentItems.containsKey(cartItemId)) {
//                    throw Exception("Item not found while updating amount")
//                }
//
////                if (currentItems[cartItemId]?.get("amount") == amount) {
////                    return@runTransaction
////                }
//
//                if (amount <= 0) {
//                    transaction.update(docRef, mapOf(
//                        "items.${cartItemId}" to FieldValue.delete(),
//                        "updatedAt" to System.currentTimeMillis()
//                    ))
//                    return@runTransaction
//                }
//
//                val oldAmount = (currentItems[cartItemId]?.get("amount") as Long).toInt()
//                if (oldAmount == amount) {
//                    return@runTransaction
//                }
//
//                transaction.update(docRef, mapOf(
//                    "items.${cartItemId}.amount" to amount.toLong(),
//                    "updatedAt" to System.currentTimeMillis()
//                ))
//            }.await()
//            Result.success(true)
//        } catch (e: Exception) {
//            Log.e("CartItemRepositoryImplement", "Error updating cart item amount: ", e)
//            Result.failure(e)
//        }
//    }

//    override suspend fun getCartItemInformation(
//        productId: String,
//        variantId: String
//    ): Result<Pair<Product?, ProductVariant?>> {
////        var product: Product? = null
////        try {
////            product = firestore.collection("products").document(productId).collection("variants")
////                .get()
////                .await()
////                .toObject(Product::class.java)
////        } catch (e: Exception) {
////            Log.e("CartItemRepositoryImplement", "Error getting product: ", e)
////        }
////
////        var variant: ProductVariant? = null
////
////        if (product != null) {
////            variant = product.variants.find { it.id == variantId }
////        }
////
////        if (product == null && variant == null) {
////            return Result.failure(Exception("Product and variant are both not found"))
////        }
////        return Result.success(Pair(product, variant))
//
//        return Result.success(Pair(null, null))
//    }

}