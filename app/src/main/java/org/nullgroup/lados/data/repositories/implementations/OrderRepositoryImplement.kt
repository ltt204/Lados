package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import androidx.compose.animation.core.snap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository
import org.nullgroup.lados.utilities.OrderStatus

// Firebase-specific repository example
class OrderRepositoryImplement(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : OrderRepository {
    // Create a new order
    override suspend fun createOrder(
        customerId: String,
        order: Order,
    ): Result<Boolean> {
        return try {
            val userOrdersRef = firestore
                .collection("users").document(customerId)
                .collection(Order.COLLECTION_NAME).document()
            val staffManagedOrdersRef = firestore
                .collection(Order.COLLECTION_NAME).document(userOrdersRef.id)
            val batch = firestore.batch()
            // Don't know why firestore still stores @Exclude fields
            batch.set(userOrdersRef, order.copy(orderProducts = listOf()))
            batch.set(staffManagedOrdersRef, order.copy(orderProducts = listOf()))
            order.orderProducts.forEach { orderProduct ->
                val userOrderProductRef = userOrdersRef
                    .collection(OrderProduct.COLLECTION_NAME).document()
                batch.set(userOrderProductRef, orderProduct)
                val staffManagedOrderProductRef = staffManagedOrdersRef
                    .collection(OrderProduct.COLLECTION_NAME).document(userOrderProductRef.id)
                batch.set(staffManagedOrderProductRef, orderProduct)
            }
            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update order status
    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus
    ): Result<Boolean> {
        // TODO: Check later if this is the correct way to update the order status
        try {
            val orderRef = firestore.collection("orders").document(orderId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(orderRef)
                if (!snapshot.exists()) {
                    throw Exception("Order not found")
                }
                val firebaseStatusLog = snapshot.toObject(Order::class.java)?.orderStatusLog
                    ?: mapOf()
                val statusLog =
                    firebaseStatusLog.mapKeys { OrderStatus.valueOf(it.key) }.toMutableMap()
                statusLog[newStatus] = System.currentTimeMillis()
                val newStatusLog = statusLog.mapKeys { it.key.name }
                transaction.update(orderRef, newStatusLog)
            }.await()
            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun getOrders(): Flow<List<Order>> = callbackFlow {
        // TODO: Replace mockUserEmail with the actual user email
//        val userEmail = firebaseAuth.currentUser?.email!!
        val mockUserEmail = "admin@test.com"
//        val orderRef = firestore.collection("users").document(mockUserEmail).collection("orders")
        Log.d("OrderRepositoryImplement", "getOrders: $mockUserEmail")
        val orderRef = firestore.collection("orders")

        val subscription = orderRef.addSnapshotListener { snapshot, e ->
            Log.d("OrderRepositoryImplement", "getOrders: $snapshot")
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val orders =
                    snapshot.documents.mapNotNull { it.toObject(Order::class.java) }.filter {
                        it.customerId == mockUserEmail
                    }
//
//                orders.forEach { order ->
//                    val orderProductRef = firestore.collection("orders").document(order.orderId)
//                        .collection("orderProducts")
//
//                }
                Log.d("OrderRepositoryImplement", "getOrders: $orders")
                trySend(orders).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }

    override fun getOrderById(orderId: String): Flow<Order> = callbackFlow {
        // TODO: Replace mockUserEmail with the actual user email
//        val userEmail = firebaseAuth.currentUser?.email!!
        val mockUserEmail = "admin@test.com"
//        val orderRef = firestore.collection("users").document(mockUserEmail).collection("orders")
        Log.d("OrderRepositoryImplement", "getOrderById: $mockUserEmail")
        val orderRef = firestore.collection("orders").document(orderId)

        val subscription = orderRef.addSnapshotListener { snapshot, e ->
            Log.d("OrderRepositoryImplement", "getOrderById: $snapshot")
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val order = snapshot.toObject(Order::class.java)
                Log.d("OrderRepositoryImplement", "getOrderById: $order")
                trySend(order!!).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }
}