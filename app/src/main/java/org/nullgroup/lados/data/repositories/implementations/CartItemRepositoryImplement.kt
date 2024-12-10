package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository

class CartItemRepositoryImplement(
    private val firestore: FirebaseFirestore,
): CartItemRepository {
    private val userCollectionName = "users"

    override fun getCartItemsAsFlow(customerId: String): Flow<List<CartItem>> = callbackFlow {
        val listener = firestore.collection(userCollectionName)
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
    override suspend fun addCartItemToCart(
        customerId: String,
        cartItem: CartItem
    ): Result<Boolean> {
        return try {
            val cartCollectionRef = firestore
                .collection(userCollectionName).document(customerId)
                .collection(CartItem.COLLECTION_NAME)
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
        customerId: String,
        cartItemIds: List<String>
    ): Result<Boolean> {
        return try {
            val customerRef = firestore.collection(userCollectionName).document(customerId)
            val batch = firestore.batch()
            cartItemIds.forEach { itemId ->
                batch.delete(customerRef.collection(CartItem.COLLECTION_NAME).document(itemId))
            }
            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error removing cart items: ", e)
            Result.failure(e)
        }
    }

    override suspend fun updateCartItemsAmount(
        customerId: String,
        adjustmentInfo: List<Pair<String, Int>>
    ): Result<Boolean> {
        if (adjustmentInfo.isEmpty()) {
            return Result.success(true)
        }
        return try {
            val customerRef = firestore.collection(userCollectionName).document(customerId)
            val batch = firestore.batch()
            adjustmentInfo.forEach { (itemId, amount) ->
                if (amount <= 0) {
                    batch.delete(
                        customerRef.collection(CartItem.COLLECTION_NAME).document(itemId)
                    )
                } else {
                    batch.update(
                        customerRef.collection(CartItem.COLLECTION_NAME).document(itemId),
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
}