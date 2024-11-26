package org.nullgroup.lados.data.models

import java.util.UUID

// Enum for order status
enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPING,
    DELIVERED,
    CANCELLED,
    RETURNED
}

// Order data class
data class Order(
    val orderId: String = UUID.randomUUID().toString(),
    val customerId: String,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val orderProducts: List<OrderProduct> = listOf()
) {
    companion object {
        const val COLLECTION_PATH = "orders"

        fun fromMap(map: Map<String, Any>): Order {
            @Suppress("UNCHECKED_CAST")
            return Order(
                orderId = map["orderId"] as String,
                customerId = map["customerId"] as String,
                status = OrderStatus.valueOf(map["status"] as String),
                createdAt = (map["createdAt"] as Number).toLong(),
                updatedAt = (map["updatedAt"] as Number).toLong(),
                orderProducts = (map["orderProducts"] as List<Map<String, Any>>)
                    .map { orderProductData ->
                        OrderProduct(
                            id = orderProductData["id"] as String,
                            orderId = orderProductData["orderId"] as String,
                            productId = orderProductData["productId"] as String,
                            variantId = orderProductData["variantId"] as String,
                            amount = (orderProductData["amount"] as Number).toInt()
                        )
                    }
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "orderId" to orderId,
            "customerId" to customerId,
            "status" to status.name,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "orderProducts" to orderProducts.map { it.toMap() }
        )
    }
}

// OrderProduct data class
data class OrderProduct(
    val id: String = UUID.randomUUID().toString(),
    val orderId: String,
    val productId: String,
    val variantId: String,
    val amount: Int
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "orderId" to orderId,
            "productId" to productId,
            "variantId" to variantId,
            "amount" to amount
        )
    }
}