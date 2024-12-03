package org.nullgroup.lados.data.repositories.interfaces

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.OrderStatus

interface OrderRepository {
    fun createOrder(customerId: String, orderProducts: List<OrderProduct>): Result<Boolean>
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Boolean>

    fun getOrders(): Flow<List<Order>>
    fun getOrderById(orderId: String): Flow<Order>
}