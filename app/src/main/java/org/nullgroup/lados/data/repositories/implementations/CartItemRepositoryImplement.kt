package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Cart
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository

class CartItemRepositoryImplement(
    private val firestore: FirebaseFirestore,
): CartItemRepository {
    override suspend fun getOrInitializeCart(userId: String): Result<Cart> {
        val documentRef = firestore.collection(Cart.COLLECTION_PATH).document(userId)

        return try {
            var document = documentRef.get().await()
            if (document.exists()) {
                Log.d("CartItemRepositoryImplement", "Cart already exists")
                Result.success(Cart.fromMap(document.data!!))
            } else {
                val cart = Cart(customerId = userId)

                firestore.runTransaction { transaction ->
                    transaction.set(documentRef, cart.toMap())
                }.await()
                Result.success(cart)
            }
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error initializing cart: ", e)
            Result.failure(e)
        }

    }

    override suspend fun getCartItems(cartId: String): Result<List<CartItem>> {
//        return Result.success(listOf(
//            CartItem(
//                productId = "prod-12345",
//                variantId = "var-001",
//                amount = 2,
//                addedAt = System.currentTimeMillis()
//            ),
//            CartItem(
//                productId = "prod-12345",
//                variantId = "var-002",
//                amount = 1,
//                addedAt = System.currentTimeMillis() - 60 * 60 * 1000 // Added an hour ago
//            ),
//        ))

        @Suppress("UNCHECKED_CAST")
        return try {
            val querySnapshot = firestore.collection(Cart.COLLECTION_PATH)
                .document(cartId)
                .get()
                .await()
            if (querySnapshot.exists()) {
                val cartItems = querySnapshot.get("items") as? Map<String, Map<String, Any>>
                    ?: mapOf()
                Result.success(cartItems.values.map {
                    CartItem.fromMap(it)
                })
            } else {
                Result.failure(Exception("Cart not found"))
            }
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error getting cart items: ", e)
            Result.failure(e)
        }
    }

    /**
    * Add a cart item to the cart.
    * If the item has "already existed", remove it and add the new one
    **/
    override suspend fun addCartItemToCart(
        cartId: String,
        cartItem: CartItem
    ): Result<Boolean> {

        @Suppress("UNCHECKED_CAST")
        return try {
            val cartDocRef = firestore.collection(Cart.COLLECTION_PATH).document(cartId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(cartDocRef)
                if (!snapshot.exists()) {
                    throw Exception("Cart not found")
                }
                val currentItems = snapshot.get("items") as? Map<String, Map<String, Any>> ?: mapOf()
                val existingItemKey = currentItems
                    .filter {
                        it.value["productId"] == cartItem.productId &&
                        it.value["variantId"] == cartItem.variantId
                    }
                    .map { it.key }
                    .firstOrNull()

                if (existingItemKey != null) {
                    transaction.update(cartDocRef, mapOf(
                        "items.$existingItemKey.amount" to cartItem.amount,
                        "items.$existingItemKey.addedAt" to cartItem.addedAt,
                        "updatedAt" to System.currentTimeMillis()
                    ))
                } else {
                    transaction.update(cartDocRef, mapOf(
                        "items.${cartItem.id}" to cartItem.toMap(),
                        "updatedAt" to System.currentTimeMillis()
                    ))
                }

//                transaction.update(cartDocRef, mapOf(
//                    "items.${cartItem.id}" to cartItem.toMap(),
//                    "updatedAt" to System.currentTimeMillis()
//                ))
            }.await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error adding cart item: ", e)
            Result.failure(e)
        }
    }

    override suspend fun removeCartItemFromCart(
        cartId: String,
        cartItemId: String
    ): Result<Boolean> {

        @Suppress("UNCHECKED_CAST")
        return try {
            val docRef = firestore.collection(Cart.COLLECTION_PATH)
                .document(cartId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw Exception("Cart not found")
                }
                val currentItems = snapshot.get("items") as? Map<String, Map<String, Any>>
                    ?: mapOf()
                if (currentItems.containsKey(cartItemId)) {
                    transaction.update(docRef, mapOf(
                        "items.${cartItemId}" to FieldValue.delete(),
                        "updatedAt" to System.currentTimeMillis()
                    ))
                    return@runTransaction
                } else {
                    throw Exception("Item not found while removing")
                }

            }.await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error removing cart item: ", e)
            Result.failure(e)
        }
    }

    /**
     * If amount is less than 1, remove the item from the cart
     *  */
    override suspend fun updateCartItemAmount(
        cartId: String,
        cartItemId: String,
        amountDelta: Int
    ): Result<Boolean> {
        if (amountDelta == 0) {
            return Result.success(true)
        }

        @Suppress("UNCHECKED_CAST")
        return try {
            val docRef = firestore.collection(Cart.COLLECTION_PATH)
                .document(cartId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw Exception("Cart not found")
                }
                val currentItems = snapshot.get("items") as? Map<String, Map<String, Any>>
                    ?: mapOf()

                if (!currentItems.containsKey(cartItemId)) {
                    throw Exception("Item not found while updating amount")
                }

//                if (currentItems[cartItemId]?.get("amount") == amount) {
//                    return@runTransaction
//                }

                val amount = (currentItems[cartItemId]?.get("amount") as Long).toInt() + amountDelta
                if (amount <= 0) {
                    transaction.update(docRef, mapOf(
                        "items.${cartItemId}" to FieldValue.delete(),
                        "updatedAt" to System.currentTimeMillis()
                    ))
                    return@runTransaction
                }

                transaction.update(docRef, mapOf(
                    "items.${cartItemId}.amount" to FieldValue.increment(amountDelta.toLong()),
                    "updatedAt" to System.currentTimeMillis()
                ))
            }.await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error updating cart item amount: ", e)
            Result.failure(e)
        }
    }

    override suspend fun getCartItemInformation(
        productId: String,
        variantId: String
    ): Result<Pair<Product?, ProductVariant?>> {
        var product: Product? = null
        try {
            product = firestore.collection("products").document(productId).get()
                .await()
                .toObject(Product::class.java)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error getting product: ", e)
        }

        var variant: ProductVariant? = null

        if (product != null) {
            variant = product.variants.find { it.id == variantId }
        }

        if (product == null && variant == null) {
            return Result.failure(Exception("Product and variant are both not found"))
        }
        return Result.success(Pair(product, variant))

    }

}