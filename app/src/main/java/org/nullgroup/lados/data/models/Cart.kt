package org.nullgroup.lados.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.util.UUID

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