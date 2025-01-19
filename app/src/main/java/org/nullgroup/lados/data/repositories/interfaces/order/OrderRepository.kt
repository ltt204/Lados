package org.nullgroup.lados.data.repositories.interfaces.order

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.utilities.OrderStatus
import java.util.Date

interface OrderRepository {
    fun getOrdersForAdmin(): Flow<List<Order>>

    fun getOrders(): Flow<List<Order>>
    fun getOrderById(orderId: String): Flow<Order>

    suspend fun createOrder(customerId: String, order: Order): Result<Pair<Boolean, Map<OrderProduct, Int>>>
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Boolean>
    suspend fun getAllOrders(startDate: Date, endDate: Date): Result<List<Order>>
    suspend fun getAllOrdersFromFirestore(): Result<List<Order>>
}