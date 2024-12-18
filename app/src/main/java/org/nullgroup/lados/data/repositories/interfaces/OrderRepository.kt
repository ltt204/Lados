package org.nullgroup.lados.data.repositories.interfaces

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.utilities.OrderStatus

interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
    fun getOrderById(orderId: String): Flow<Order>

    suspend fun createOrder(customerId: String, order: Order): Result<Pair<Boolean, Map<OrderProduct, Int>>>
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Boolean>
}