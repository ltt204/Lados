package org.nullgroup.lados.data.repositories.interfaces

import org.nullgroup.lados.data.models.Cart
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant

interface CartItemRepository {
    suspend fun getOrInitializeCart(userId: String): Result<Cart>
    suspend fun getCartItems(cartId: String): Result<List<CartItem>>
    suspend fun addCartItemToCart(cartId: String, cartItem: CartItem): Result<Boolean>
    suspend fun removeCartItemFromCart(cartId: String, cartItemId: String): Result<Boolean>
    suspend fun updateCartItemAmount(cartId: String, cartItemId: String, amountDelta: Int): Result<Boolean>

    suspend fun getCartItemInformation(productId: String, variantId: String): Result<Pair<Product?, ProductVariant?>>
}