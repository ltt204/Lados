package org.nullgroup.lados.data.repositories.interfaces

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.OrderStatus
import java.util.Enumeration

interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
    fun getOrderById(orderId: String): Flow<Order>
    
//    suspend fun createOrderForCustomer(customerId: String, ): Result<Order?>
//    suspend fun updateOrderOnCreation(
//        customerId: String,
//        orderId: String,
//        orderProducts: List<OrderProduct>,
//        orderTotal: Double
//    ): Result<Boolean>
    suspend fun createOrder(customerId: String, order: Order): Result<Pair<Boolean, Map<OrderProduct, Int>>>
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Boolean>
}