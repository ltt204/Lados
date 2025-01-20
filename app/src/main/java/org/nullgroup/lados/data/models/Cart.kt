package org.nullgroup.lados.data.models

import com.google.firebase.firestore.DocumentId
import java.util.UUID
import kotlin.math.max

// Data class for cart items
data class CartItem(
    @DocumentId val id: String = UUID.randomUUID().toString(),
    val productId: String = "",  // Reference to product document ID
    val variantId: String = "",  // Reference to variant document ID
    val amount: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val COLLECTION_NAME = "cartItems"
    }
}

data class CheckingOutItem(
    val cartItem: CartItem = CartItem(),
    val product: Product = Product(),
    val variant: ProductVariant = ProductVariant()
) {
    companion object {
        const val COLLECTION_NAME = "lastCheckingOutItems"

//        fun fromMap(map: Map<String, Any>?): CheckingOutItem? {
//            if (map == null) {
//                return null
//            }
//            return CheckingOutItem(
//                cartItem = map["cartItem"] as CartItem,
//                product = map["product"] as Product,
//                variant = map["variant"] as ProductVariant
//            )
//        }
    }

//    fun toMap(): Map<String, Any> {
//        return mapOf(
//            "cartItem" to cartItem,
//            "product" to product,
//            "variant" to variant
//        )
//    }
}

data class CheckoutInfo(
    val subtotal: Double = 0.0,
    val productDiscount: Double = 0.0,
    val orderDiscount: Double = 0.0,
) {
    val total: Double
        get() = max(subtotal - productDiscount - orderDiscount, 0.0)

    companion object {
        const val FIELD_NAME = "lastCheckoutInfo"
    }
}

// Main Cart data class
//data class Cart(
//    val customerId: String = "",  // Reference to customer document ID
//    @Exclude
//    val items: Map<String, CartItem> = mapOf(),
//    val lastUpdatedAt: Long = System.currentTimeMillis(),
//) {
//    // Constructor to create empty cart for a customer
//    constructor(customerId: String) : this(
//        customerId = customerId,
//        items = mapOf()
//    )
//
//    companion object {
//        const val COLLECTION_NAME = "carts"
//    }
//}