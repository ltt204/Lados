package org.nullgroup.lados.data.models
import java.util.UUID

// Data class for WishList item that combines WishList_Product properties
data class WishListItem(
    val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val timestamp: Long = System.currentTimeMillis()
)

// Main WishList data class
data class WishList(
    val id: String = UUID.randomUUID().toString(),
    val customerId: String,
    val items: Map<String, WishListItem> = mapOf(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // Constructor to create empty wishlist for a customer
    constructor(customerId: String) : this(
        customerId = customerId,
        items = mapOf()
    )

    companion object {
        // Firebase path constant
        const val COLLECTION_PATH = "wishlists"

        // Convert Firebase Map to WishList object
        fun fromMap(map: Map<String, Any>): WishList {
            @Suppress("UNCHECKED_CAST")
            return WishList(
                id = map["id"] as String,
                customerId = map["customerId"] as String,
                items = (map["items"] as? Map<String, Map<String, Any>>)?.mapValues {
                    WishListItem(
                        id = it.value["id"] as String,
                        productId = it.value["productId"] as String,
                        timestamp = it.value["timestamp"] as Long
                    )
                } ?: mapOf(),
                createdAt = map["createdAt"] as Long,
                updatedAt = map["updatedAt"] as Long
            )
        }
    }

    // Convert WishList to Firebase Map
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "id" to id,
            "customerId" to customerId,
            "items" to items.mapValues { it.value.toMap() },
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}

// Extension function to convert WishListItem to Map
fun WishListItem.toMap(): Map<String, Any> {
    return hashMapOf(
        "id" to id,
        "productId" to productId,
        "timestamp" to timestamp
    )
}

/*

// Create a new wishlist
val wishlist = WishList(customerId = "customer123")

// Add item to wishlist
val newItem = WishListItem(productId = "product456")
val updatedItems = wishlist.items + (newItem.id to newItem)
val updatedWishlist = wishlist.copy(
    items = updatedItems,
    updatedAt = System.currentTimeMillis()
)

// Save to Firebase
Firebase.firestore
    .collection(WishList.COLLECTION_PATH)
    .document(wishlist.id)
    .set(updatedWishlist.toMap())

 */
