package org.nullgroup.lados.data.repositories.implementations.order

import android.util.Log
import com.google.firebase.Timestamp
import androidx.compose.animation.core.snap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.remote.models.ProductVariantRemoteModel
import org.nullgroup.lados.data.repositories.interfaces.order.OrderRepository
import org.nullgroup.lados.utilities.OrderStatus
import java.util.Date

// Firebase-specific repository example
class OrderRepositoryImplement(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : OrderRepository {
    // Create a new order
    override suspend fun createOrder(
        customerId: String,
        order: Order,
    ): Result<Pair<Boolean, Map<OrderProduct, Int>>> {
        val insufficientOrderItems = mutableMapOf<OrderProduct, Int>()
        // TODO - Advance: Resolve contention issue
        //      Using a server-side queue to handle stock update transactions
        var transactionResult: Result<Boolean> = Result.failure(Exception("Transaction failed"))
        try {
            firestore.runTransaction { transaction ->
                val failReason = mutableListOf<String>()
                val variantRefs = mutableListOf<DocumentReference>()
                for (orderProduct in order.orderProducts) {
                    val productRef =
                        firestore.collection("products").document(orderProduct.productId)
                    val variantRef =
                        productRef.collection("variants").document(orderProduct.variantId)
                    val snapshot = transaction.get(variantRef)
                    val variant = snapshot.toObject(ProductVariantRemoteModel::class.java)
                    if (variant == null) {
                        // Kind of "redundant", but just in case
                        failReason.add("Product/Variant not found for order item ${orderProduct.productId}")
                        continue
                    }
                    if (variant.quantityInStock < orderProduct.amount) {
                        failReason.add("Not enough stock for order item ${orderProduct.productId}")
                        insufficientOrderItems[orderProduct] = variant.quantityInStock
                        continue
                    }
                    variantRefs.add(variantRef)
                }

                if (failReason.isNotEmpty()) {
//                    throw FirebaseFirestoreException(
//                        "Failed to create order",
//                        FirebaseFirestoreException.Code.ABORTED,
//                        Exception(failReason.joinToString("\n"))
//                    )

                    throw Exception(failReason.joinToString("\n"))
                }

                for ((orderProduct, variantRef) in order.orderProducts.zip(variantRefs)) {
                    transaction.update(
                        variantRef,
                        "quantityInStock",
                        FieldValue.increment(-orderProduct.amount.toLong())
                    )
                }
            }.addOnSuccessListener {
                transactionResult = Result.success(true)
            }.addOnFailureListener { e ->
                transactionResult = Result.failure(e)
            }.await()
        } catch (e: Exception) {
            transactionResult = Result.failure(e)
        }

        if (transactionResult.isSuccess) {
//                val userOrdersRef = firestore
//                    .collection("users").document(customerId)
//                    .collection(Order.COLLECTION_NAME).document()
//                val staffManagedOrdersRef = firestore
//                    .collection(Order.COLLECTION_NAME).document(userOrdersRef.id)
//                val batch = firestore.batch()
//                // Don't know why firestore still stores @Exclude fields
//                batch.set(userOrdersRef, order.copy(orderProducts = listOf()))
//                batch.set(staffManagedOrdersRef, order.copy(orderProducts = listOf()))
//                order.orderProducts.forEach { orderProduct ->
//                    val userOrderProductRef = userOrdersRef
//                        .collection(OrderProduct.COLLECTION_NAME).document()
//                    batch.set(userOrderProductRef, orderProduct)
//                    val staffManagedOrderProductRef = staffManagedOrdersRef
//                        .collection(OrderProduct.COLLECTION_NAME).document(userOrderProductRef.id)
//                    batch.set(staffManagedOrderProductRef, orderProduct)
//                }
//                batch.commit().await()
//                Result.success(true)

            try {
                val userOrdersRef = firestore
                    .collection("users").document(customerId)
                    .collection(Order.COLLECTION_NAME).document()
                val staffManagedOrdersRef = firestore
                    .collection(Order.COLLECTION_NAME).document(userOrdersRef.id)
                val batch = firestore.batch()
                batch.set(userOrdersRef, order)
                batch.set(staffManagedOrdersRef, order)
                batch.commit().await()
                return Result.success(true to mutableMapOf())
            } catch (e: Exception) {
                return Result.failure(e)
            }
        } else {
            // Result.failure(transactionResult.exceptionOrNull()!!)
            return if (insufficientOrderItems.isEmpty()) {
                Result.failure(transactionResult.exceptionOrNull()!!)
            } else {
                Result.success(false to insufficientOrderItems)
            }
        }
    }

//    override suspend fun updateAllOrderStatus() {
//        try {
//            val ordersRef = firestore.collection("orders")
//
//            ordersRef.get()
//                .addOnSuccessListener { snapshot ->
//                    for (document in snapshot.documents) {
//                        val order = document.toObject(Order::class.java)!!
//                        val currentStatus = getLatestStatus(order)
//
//                        val userOrdersRef = firestore.collection("users")
//                            .document(order.customerId)
//                            .collection("orders").document(order.orderId)
//
//                        document.reference.update("currentStatus", currentStatus.name)
//                            .addOnSuccessListener {
//                                println("ok")
//                            }
//                            .addOnFailureListener {
//                                println("no")
//                            }
//                    }
//                }
//                .addOnFailureListener {
//                    println("no no")
//                }
//        } catch (error: Exception) {
//            throw error
//        }
//    }

    // Update order status
    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus,
    ): Result<Boolean> {
        // TODO: Check later if this is the correct way to update the order status
        try {
            val orderRef = firestore.collection("orders").document(orderId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(orderRef)
                if (!snapshot.exists()) {
                    throw Exception("Order not found")
                }

                val order = snapshot.toObject(Order::class.java)
                val userOrderRef = firestore.collection("users")
                    .document(order?.customerId!!)
                    .collection("orders")
                    .document(orderId)

                if (!isValidStatusTransition(getLatestStatus(order), newStatus)) {
                    throw Exception("Invalid status transition")
                }

                val firebaseStatusLog = order.orderStatusLog
                val statusLog =
                    firebaseStatusLog.mapKeys { OrderStatus.valueOf(it.key) }.toMutableMap()
                statusLog[newStatus] = System.currentTimeMillis()
                val newStatusLog = statusLog.mapKeys { it.key.name }
                transaction.update(
                    orderRef, mapOf(
                        "orderStatusLog" to newStatusLog,
                        "currentStatus" to newStatus.name,
                        "lastUpdatedAt" to System.currentTimeMillis(),
                    )
                )
                transaction.update(
                    userOrderRef, mapOf(
                        "orderStatusLog" to newStatusLog,
                        "currentStatus" to newStatus.name,
                        "lastUpdatedAt" to System.currentTimeMillis(),
                    )
                )
            }.await()

            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun getOrdersForAdmin(): Flow<List<Order>> {
        return callbackFlow {
            val orderRef = firestore.collection("orders")

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
    }

    private fun isValidStatusTransition(
        currentStatus: OrderStatus,
        newStatus: OrderStatus,
    ): Boolean {
        return when (currentStatus) {
            OrderStatus.CREATED -> OrderStatus.CONFIRMED == newStatus || OrderStatus.CANCELLED == newStatus
            OrderStatus.CONFIRMED -> OrderStatus.SHIPPED == newStatus
            OrderStatus.SHIPPED -> OrderStatus.DELIVERED == newStatus
            OrderStatus.DELIVERED -> OrderStatus.RETURNED == newStatus
            else -> false
        }
    }

    private fun getLatestStatus(order: Order): OrderStatus {
        return order.orderStatusLog.maxByOrNull { it.value }?.let {
            OrderStatus.valueOf(it.key)
        } ?: OrderStatus.CREATED
    }

    override fun getOrders(): Flow<List<Order>> = callbackFlow {
        val orderRef = firestore.collection("users").document(firebaseAuth.currentUser?.uid!!)
            .collection("orders")

        val subscription = orderRef.addSnapshotListener { snapshot, e ->
            Log.d("OrderRepositoryImplement", "getOrders: $snapshot")
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val orders =
                    snapshot.documents.mapNotNull { it.toObject(Order::class.java) }.filter {
                        it.customerId == firebaseAuth.currentUser?.uid!!
                    }
                Log.d("OrderRepositoryImplement", "getOrders: $orders")
                trySend(orders).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }

    override fun getOrdersForStaff() = callbackFlow {
        val subscription = firestore.collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val orders = snapshot.toObjects(Order::class.java)
                    trySend(orders)
                }
            }

        awaitClose {
            subscription.remove()
        }
    }

    override fun getOrderById(orderId: String): Flow<Order> = callbackFlow {
        val orderRef =
            firestore.collection("users").document(firebaseAuth.currentUser?.uid!!)
                .collection("orders").document(orderId)

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

    override suspend fun getAllOrders(startDate: Date, endDate: Date): Result<List<Order>> {
        return try {
            val ordersRef = firestore.collection("orders")
            val querySnapshot = ordersRef
                .whereGreaterThanOrEqualTo("lastUpdatedAt", startDate.time)
                .whereLessThanOrEqualTo("lastUpdatedAt", endDate.time)
                .whereEqualTo("currentStatus", "PAID") // Only include orders with status PAID
                .get()
                .await()

            val orders = querySnapshot.documents.mapNotNull { it.toObject(Order::class.java) }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllOrdersFromFirestore(): Result<List<Order>> {
        return try {
            val orders = firestore.collection("orders").get().await().toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrderByStatus(
        status: OrderStatus,
        limit: Long,
        lastDocument: DocumentSnapshot?,
    ): Result<OrderPage> {
        return try {
            val query = firestore.collection("orders")
                .whereEqualTo("currentStatus", status.name)
                .orderBy("lastUpdatedAt", Query.Direction.DESCENDING)
                .limit(limit)

            val finalQuery = lastDocument?.let {
                query.startAfter(it)
            } ?: query

            val snapshot = finalQuery.get().await()
            val orders = snapshot.toObjects(Order::class.java)
            val lastDoc = snapshot.documents.lastOrNull()

            Result.success(OrderPage(orders, lastDoc))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getOrderByIdForStaff(orderId: String) = callbackFlow {
        val orderRef = firestore.collection("orders").document(orderId)

        val subscription = orderRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val order = snapshot.toObject(Order::class.java)
                trySend(order!!).isSuccess
            }
        }

        awaitClose {
            subscription.remove()
        }
    }

    override suspend fun searchOrdersById(query: String): Result<List<Order>> =
        withContext(Dispatchers.IO) {
            try {
                val orderRef = firestore.collection("orders")

                val orderIdQuery = orderRef.whereGreaterThanOrEqualTo(FieldPath.documentId(), query)
                    .whereLessThanOrEqualTo(FieldPath.documentId(), query + '\uf8ff')
                    .limit(6)
                    .get()
                    .await()

                val results = orderIdQuery.toObjects(Order::class.java)
                Result.success(results)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

data class OrderPage(
    val orders: List<Order>,
    val lastDocument: DocumentSnapshot? = null,
)