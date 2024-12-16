package org.nullgroup.lados.data.repositories.interfaces

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.CartItem

interface CartItemRepository {
    fun getCartItemsAsFlow(customerId: String): Flow<List<CartItem>>
    suspend fun addCartItemToCart(customerId: String, cartItem: CartItem): Result<Boolean>
    suspend fun removeCartItemsFromCart(customerId: String, cartItemIds: List<String>): Result<Boolean>
    suspend fun updateCartItemsAmount(
        customerId: String,
        adjustmentInfo: List<Pair<String, Int>>
    ): Result<Boolean>
}