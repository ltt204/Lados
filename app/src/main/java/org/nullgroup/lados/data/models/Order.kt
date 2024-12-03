package org.nullgroup.lados.data.models

import java.util.UUID

// Enum for order status
enum class OrderStatus {
    CREATED,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
        // Optional feature: Customer can cancel the order
    RETURNED,
        // Optional feature: Customer can return the products after delivery
        // if they are not satisfied
        // Maybe unused
}

// Order data class
data class Order(
    val orderId: String = UUID.randomUUID().toString(),
    val customerId: String,
    val orderStatusLog: Map<OrderStatus, Long> = mapOf(
        OrderStatus.CREATED to System.currentTimeMillis()
    ),
    val orderProducts: List<OrderProduct> = listOf(),
    val orderTotal: Double = orderProducts.sumOf { it.totalPrice },
        // Maybe different that the total of individual products
        // If discount is applied
) {
    companion object {
        const val COLLECTION_PATH = "orders"

        fun fromMap(map: Map<String, Any>): Order {
            @Suppress("UNCHECKED_CAST")
            return Order(
                orderId = map["orderId"] as String,
                customerId = map["customerId"] as String,
                orderStatusLog = (map["orderStatusLog"] as Map<String, Long>)
                    .mapKeys { OrderStatus.valueOf(it.key) },
                orderProducts = (map["orderProducts"] as List<Map<String, Any>>)
                    .map { orderProductData ->
                        OrderProduct(
                            id = orderProductData["id"] as String,
                            orderId = orderProductData["orderId"] as String,
                            productId = orderProductData["productId"] as String,
                            variantId = orderProductData["variantId"] as String,
                            amount = (orderProductData["amount"] as Number).toInt(),
                            totalPrice = orderProductData["totalPrice"] as Double,
                        )
                    },
                orderTotal = (map["orderTotal"] as Double),
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "orderId" to orderId,
            "customerId" to customerId,
            "orderStatusLog" to orderStatusLog.mapKeys { it.key.name },
            "orderProducts" to orderProducts.map { it.toMap() },
            "orderTotal" to orderTotal,
        )
    }
}

// OrderProduct data class
data class OrderProduct(
    val id: String = UUID.randomUUID().toString(),
    val orderId: String,
    val productId: String,
    val variantId: String,
    val amount: Int,
    val totalPrice: Double,
        // Maybe different that the total of individual products
        // If discount is applied
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "orderId" to orderId,
            "productId" to productId,
            "variantId" to variantId,
            "amount" to amount,
            "totalPrice" to totalPrice
        )
    }
}