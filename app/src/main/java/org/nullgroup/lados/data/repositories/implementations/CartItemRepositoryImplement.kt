package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository

class CartItemRepositoryImplement(
    private val firestore: FirebaseFirestore,
): CartItemRepository {
    override suspend fun getCartItems(cartId: String): Result<List<CartItem>> {
        return Result.success(listOf(
            CartItem(
                productId = "prod-12345",
                variantId = "var-001",
                amount = 2,
                addedAt = System.currentTimeMillis()
            ),
            CartItem(
                productId = "prod-12345",
                variantId = "var-002",
                amount = 1,
                addedAt = System.currentTimeMillis() - 60 * 60 * 1000 // Added an hour ago
            ),
        ))
    }

    override suspend fun addCartItemToCart(
        cartId: String,
        cartItem: CartItem
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun removeCartItemFromCart(
        cartId: String,
        cartItemId: String
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCartItemAmount(
        cartId: String,
        cartItemId: String,
        amount: Int
    ): Result<Boolean> {
        TODO("Not yet implemented")
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