package org.nullgroup.lados.data.repositories.implementations.wishlist

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.WishlistItem
import org.nullgroup.lados.data.repositories.interfaces.wishlist.WishlistItemRepository

class WishlistItemRepositoryImplement(
    private val firestore: FirebaseFirestore,
): WishlistItemRepository {
    private val userCollectionName = "users"

    override fun getWishlistItems(customerId: String): Flow<List<WishlistItem>> = callbackFlow {
        val wishlistRef = firestore
            .collection(userCollectionName)
            .document(customerId)
            .collection(WishlistItem.COLLECTION_NAME)
        val listener = wishlistRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot == null) {
                return@addSnapshotListener
            }

            val wishlistItems = snapshot.documents.mapNotNull {
                it.toObject(WishlistItem::class.java)
            }
            trySend(wishlistItems)
        }
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun addItemsToWishlist(
        customerId: String,
        wishlistItems: List<WishlistItem>
    ): Result<Boolean> {
        if (wishlistItems.isEmpty()) {
            return Result.success(true)
        }
        return try {
            val wishlistCollectionRef = firestore
                .collection(userCollectionName)
                .document(customerId)
                .collection(WishlistItem.COLLECTION_NAME)

            // Wep, can't query on transaction
//            firestore.runTransaction { transaction ->
//                val nonExistingItems = wishlistItems.mapNotNull { item ->
//                    // whereEqualTo("productId", item.productId) does not work
//                    val existingItemRef = transaction.get(wishlistCollectionRef.document(item.id))
//                    if (!existingItemRef.exists()) {
//                        item
//                    }
//                    else {
//                        null
//                    }
//                }
//
//                nonExistingItems.forEach {
//                    transaction.set(wishlistCollectionRef.document(), it)
//                }
//            }.addOnSuccessListener {
//                Log.d("WishlistItemRepositoryImplement", "Transaction success")
//                Result.success(true)
//            }.addOnFailureListener {
//                Log.e("WishlistItemRepositoryImplement", "Transaction failed: ", it)
//                throw Exception("transaction failed with message \"${it.message}\"")
//            }

            val unlistedItems = wishlistItems.filter {
                val existingItemRef = wishlistCollectionRef
                    .whereEqualTo("productId", it.productId)
                    .get().await()
                    .documents.firstOrNull()
                existingItemRef == null
            }
            val batch = firestore.batch()
            unlistedItems.forEach {
                batch.set(wishlistCollectionRef.document(), it)
            }
            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("WishlistItemRepositoryImplement", "Error adding to wishlist: ", e)
            Result.failure(e)
        }
    }

    override suspend fun removeItemsFromWishlist(
        customerId: String,
        wishlistItemIds: List<String>
    ): Result<Boolean> {
        if (wishlistItemIds.isEmpty()) {
            return Result.success(true)
        }
        return try {
            val wishlistCollectionRef = firestore
                .collection(userCollectionName)
                .document(customerId)
                .collection(WishlistItem.COLLECTION_NAME)
            val batch = firestore.batch()
            wishlistItemIds.forEach { itemId ->
                // Delete not throw even if the document does not exist, so no need to check
                batch.delete(wishlistCollectionRef.document(itemId))
            }
            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("WishlistItemRepositoryImplement", "Error remove from wishlist: ", e)
            Result.failure(e)
        }
    }

    override suspend fun removeProductFromWishlist(
        customerId: String,
        productId: String
    ): Result<Boolean> {
        return try {
            val wishlistCollectionRef = firestore
                .collection(userCollectionName)
                .document(customerId)
                .collection(WishlistItem.COLLECTION_NAME)
            val existingItemRef = wishlistCollectionRef
                .whereEqualTo("productId", productId)
                .get().await()
                .documents.firstOrNull()
            existingItemRef?.reference?.delete()?.await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("WishlistItemRepositoryImplement", "Error remove from wishlist: ", e)
            Result.failure(e)
        }
    }

    override fun checkIfItemIsInWishlist(
        customerId: String,
        productId: String
    ): Flow<Boolean> = callbackFlow {
        val wishlistRef = firestore
            .collection(userCollectionName)
            .document(customerId)
            .collection(WishlistItem.COLLECTION_NAME)
        val listener = wishlistRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot == null) {
                return@addSnapshotListener
            }

            val isItemInWishlist = snapshot.documents.any { it.toObject(WishlistItem::class.java)?.productId == productId }
            trySend(isItemInWishlist)
        }
        awaitClose {
            listener.remove()
        }
    }

}