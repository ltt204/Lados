package org.nullgroup.lados.data.models

import java.util.UUID

// Data class for cart items
data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val productId: String,  // Reference to product document ID
    val variantId: String = "",  // Reference to variant document ID
    val amount: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
) {
    companion object {
        // Convert Firebase Map to CartItem object
        fun fromMap(map: Map<String, Any>): CartItem {
            return CartItem(
                id = map["id"] as String,
                productId = map["productId"] as String,
                variantId = map["variantId"] as String,
                amount = (map["amount"] as Long).toInt(),
                addedAt = map["addedAt"] as Long
            )
        }
    }

    // Convert CartItem to Firebase Map
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "id" to id,
            "productId" to productId,
            "variantId" to variantId,
            "amount" to amount,
            "addedAt" to addedAt,
        )
    }
}

// Main Cart data class
data class Cart(
    val customerId: String,  // Reference to customer document ID
    val items: Map<String, CartItem> = mapOf(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    // Constructor to create empty cart for a customer
    constructor(customerId: String) : this(
        customerId = customerId,
        items = mapOf()
    )

    companion object {
        // Firebase path constant
        const val COLLECTION_PATH = "carts"

        // Convert Firebase Map to Cart object
        fun fromMap(map: Map<String, Any>): Cart {
            @Suppress("UNCHECKED_CAST")
            return Cart(
                customerId = map["customerId"] as String,
                items = (map["items"] as? Map<String, Map<String, Any>>)?.mapValues {
                    CartItem(
                        id = it.value["id"] as String,
                        productId = it.value["productId"] as String,
                        amount = (it.value["amount"] as Long).toInt(),
                        addedAt = it.value["addedAt"] as Long
                    )
                } ?: mapOf(),
                createdAt = map["createdAt"] as Long,
                updatedAt = map["updatedAt"] as Long,
            )
        }
    }

    // Convert Cart to Firebase Map
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "customerId" to customerId,
            "items" to items.mapValues { it.value.toMap() },
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
        )
    }
}