package org.nullgroup.lados.data.repositories.implementations

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.OrderStatus
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository

// Firebase-specific repository example
class OrderRepositoryImplement(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
): OrderRepository {
    // Create a new order
    override fun createOrder(customerId: String, orderProducts: List<OrderProduct>): Result<Boolean> {
        val order = Order(
            customerId = customerId,
            orderProducts = orderProducts
        )

        return try {
            db.collection("orders").add(order.toMap())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update order status
    override fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Boolean> {
        try {
            db.collection("orders")
                .document(orderId)
                .update(mapOf(
                    "status" to newStatus.name,
                    "updatedAt" to System.currentTimeMillis()
                ))
            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun getOrders(): Flow<List<Order>> = callbackFlow {
        val userEmail = firebaseAuth.currentUser?.email!!
        val orderRef = db.collection("users").document(userEmail).collection("orders")

        val subscription = orderRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val orders = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
                trySend(orders).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }

    override fun getOrderById(orderId: String): Flow<Order> = callbackFlow  {
        val userEmail = firebaseAuth.currentUser?.email!!
        val orderRef = db.collection("users").document(userEmail).collection("orders")

        val subscription = orderRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val orders = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
                trySend(orders.first { it.orderId == orderId }).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }
}