package org.nullgroup.lados.data.repositories.interfaces

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.CheckingOutItem
import org.nullgroup.lados.data.models.CheckoutInfo

interface CartItemRepository {
    fun getCartItemsAsFlow(customerId: String): Flow<List<CartItem>>
    suspend fun addCartItemToCart(
//        customerId: String,
        cartItem: CartItem,
    ): Result<Boolean>
    suspend fun removeCartItemsFromCart(customerId: String, cartItemIds: List<String>): Result<Boolean>
    suspend fun updateCartItemsAmount(
        customerId: String,
        adjustmentInfo: List<Pair<String, Int>>
    ): Result<Boolean>
    suspend fun saveCheckingOutItems(
        customerId: String,
        checkingOutItems: List<CheckingOutItem>
    ): Result<Boolean>
    suspend fun getCheckingOutItemsAsFlow(customerId: String): Flow<List<CheckingOutItem>>
    suspend fun clearCheckingOutItems(customerId: String): Result<Boolean>
    suspend fun saveCheckoutInfo(customerId: String, checkoutInfo: CheckoutInfo): Result<Boolean>
    suspend fun getCheckoutInfo(customerId: String): Result<CheckoutInfo?>
    suspend fun clearCheckoutInfo(customerId: String): Result<Boolean>
}