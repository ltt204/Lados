package org.nullgroup.lados.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import org.nullgroup.lados.utilities.OrderStatus

// Order data class
data class Order(
    @DocumentId val orderId: String = "",
    val customerId: String = "",
    val orderStatusLog: Map<String, Long> = mapOf(
        OrderStatus.CREATED.name to System.currentTimeMillis()
    ),
    val orderProducts: List<OrderProduct> = listOf(),
    val orderTotal: Double = orderProducts.sumOf { it.totalPrice },
        // Maybe different that the total of individual products
        // If discount is applied
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val deliveryAddress: String = "",
    val customerPhoneNumber: String = "",
) {
    companion object {
        const val COLLECTION_NAME = "orders"
    }
}


// OrderProduct data class
data class OrderProduct(
    @DocumentId val id: String = "",
    // val orderId: String = "",
    val productId: String = "",
    val variantId: String = "",
    val amount: Int = 0,
    val totalPrice: Double = 0.0,
        // Maybe different that the total of individual products
        // If discount is applied
)
