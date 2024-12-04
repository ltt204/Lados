package org.nullgroup.lados.data.repositories.interfaces

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.Cart
import org.nullgroup.lados.data.models.CartItem

interface CartItemRepository {
    suspend fun getOrInitializeCart(customerId: String): Result<Cart>
    // suspend fun getCartItems(cartId: String): Result<List<CartItem>>
    fun getCartItemsAsFlow(customerId: String): Flow<List<CartItem>>
//    suspend fun getProductAndVariant(
//        productId: String,
//        variantId: String
//    ): Result<Pair<Product?, ProductVariant?>>
    suspend fun addCartItemToCart(cartId: String, cartItem: CartItem): Result<Boolean>
    suspend fun removeCartItemsFromCart(cartId: String, cartItemIds: List<String>): Result<Boolean>
    suspend fun updateCartItemsAmount(
        cartId: String,
        adjustmentInfo: List<Pair<String, Int>>
    ): Result<Boolean>
}