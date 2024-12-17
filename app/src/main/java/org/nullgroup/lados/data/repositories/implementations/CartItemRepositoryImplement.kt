package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.CheckingOutItem
import org.nullgroup.lados.data.models.CheckoutInfo
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository

class CartItemRepositoryImplement(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
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

                if (snapshot == null) {
                    return@addSnapshotListener
                }

                val cartItems = snapshot.documents.mapNotNull {
                    if (it.exists()) {
                        it.toObject(CartItem::class.java)
                    }
                    else {
                        it.toObject(CartItem::class.java)!!.copy(
                            amount = 0
                        )
                    }
                }
                trySend(cartItems)
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
//        customerId: String,
        cartItem: CartItem
    ): Result<Boolean> {
        return try {
            val customerId = firebaseAuth.currentUser?.uid
                ?: throw Exception("User not logged in")
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

    override suspend fun saveCheckingOutItems(
        customerId: String,
        checkingOutItems: List<CheckingOutItem>
    ): Result<Boolean> {
        return try {
            val customerRef = firestore.collection(userCollectionName).document(customerId)
            val batch = firestore.batch()
            checkingOutItems.forEach { checkingOutItem ->
                val modifiedItem = checkingOutItem.copy(
                    product = checkingOutItem.product.copy(
                        variants = emptyList(),
                        engagements = emptyList()
                    )
                )
                batch.set(
                    customerRef
                        .collection(CheckingOutItem.COLLECTION_NAME)
                        .document(checkingOutItem.cartItem.id),
                    modifiedItem
                )
            }
            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error saving checking out items: ", e)
            Result.failure(e)
        }
    }

    override suspend fun getCheckingOutItemsAsFlow(
        customerId: String
    ): Flow<List<CheckingOutItem>> = callbackFlow {
        val listener = firestore.collection(userCollectionName)
            .document(customerId)
            .collection(CheckingOutItem.COLLECTION_NAME)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val checkingOutItems = snapshot.documents.mapNotNull {
                        it.toObject(CheckingOutItem::class.java)
                    }
                    trySend(checkingOutItems)
                }
            }
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun clearCheckingOutItems(customerId: String): Result<Boolean> {
        return try {
            val customerRef = firestore.collection(userCollectionName).document(customerId)
            val batch = firestore.batch()
            val checkingOutItems = customerRef.collection(CheckingOutItem.COLLECTION_NAME).get().await()
            checkingOutItems.documents.forEach {
                batch.delete(it.reference)
            }
            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error clearing checking out items: ", e)
            Result.failure(e)
        }
    }

    override suspend fun saveCheckoutInfo(
        customerId: String,
        checkoutInfo: CheckoutInfo
    ): Result<Boolean> {
        return try {
            val customerRef = firestore.collection(userCollectionName).document(customerId)
            customerRef
                .set(
                    mapOf(CheckoutInfo.FIELD_NAME to checkoutInfo),
                    SetOptions.merge()
                )
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error saving checkout info: ", e)
            Result.failure(e)
        }
    }

    override suspend fun getCheckoutInfo(customerId: String): Result<CheckoutInfo?> {
        return try {
            val customerRef = firestore.collection(userCollectionName).document(customerId)
            val checkoutInfo = customerRef
                .get().await()
                .get(CheckoutInfo.FIELD_NAME, CheckoutInfo::class.java)
            Result.success(checkoutInfo)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error getting checkout info: ", e)
            Result.failure(e)
        }
    }

    override suspend fun clearCheckoutInfo(customerId: String): Result<Boolean> {
        return try {
            val customerRef = firestore.collection(userCollectionName).document(customerId)
            customerRef
                .update(CheckoutInfo.FIELD_NAME, FieldValue.delete())
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CartItemRepositoryImplement", "Error clearing checkout info: ", e)
            Result.failure(e)
        }
    }
}