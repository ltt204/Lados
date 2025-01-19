package org.nullgroup.lados.data.repositories.interfaces.wishlist

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.WishlistItem

interface WishlistItemRepository {
    fun getWishlistItems(customerId: String): Flow<List<WishlistItem>>
    suspend fun addItemsToWishlist(customerId: String, wishlistItems: List<WishlistItem>): Result<Boolean>
    suspend fun removeItemsFromWishlist(customerId: String, wishlistItemIds: List<String>): Result<Boolean>
    suspend fun removeProductFromWishlist(customerId: String, productId: String): Result<Boolean>
    fun checkIfItemIsInWishlist(customerId: String, productId: String): Flow<Boolean>
}