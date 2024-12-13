package org.nullgroup.lados.data.repositories.implementations

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.TransactionOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.OrderStatus
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository

// Firebase-specific repository example
class OrderRepositoryImplement(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
): OrderRepository {
    // Create a new order
    override suspend fun createOrder(
        customerId: String,
        order: Order,
        ): Result<Boolean> {
        return try {
            // TODO - Advance: Resolve contention issue
            // TODO - Using a server-side queue to handle stock update transactions
            var transactionResult: Result<Boolean> = Result.failure(Exception("Transaction failed"))
            firestore.runTransaction { transaction ->
                val failReason = mutableListOf<String>()
                val variantRefs = mutableListOf<DocumentReference>()
                for (orderProduct in order.orderProducts) {
                    val productRef = firestore.collection("products").document(orderProduct.productId)
                    val variantRef = productRef.collection("variants").document(orderProduct.variantId)
                    val snapshot = transaction.get(variantRef)
                    val variant = snapshot.toObject(ProductVariant::class.java)
                    if (variant == null) {
                        failReason.add("Product/Variant not found for order item ${orderProduct.productId}")
                        continue
                    }
                    if (variant.quantityInStock < orderProduct.amount) {
                        failReason.add("Not enough stock for order item ${orderProduct.productId}")
                        continue
                    }
                    variantRefs.add(variantRef)
                }

                if (failReason.isNotEmpty()) {
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


                val userOrdersRef = firestore
                    .collection("users").document(customerId)
                    .collection(Order.COLLECTION_NAME).document()
                val staffManagedOrdersRef = firestore
                    .collection(Order.COLLECTION_NAME).document(userOrdersRef.id)
                val batch = firestore.batch()
                batch.set(userOrdersRef, order)
                batch.set(staffManagedOrdersRef, order)
                batch.commit().await()
                Result.success(true)
            }
            else {
                Result.failure(transactionResult.exceptionOrNull()!!)
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update order status
    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Boolean> {
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
                val statusLog = firebaseStatusLog.mapKeys { OrderStatus.valueOf(it.key) }.toMutableMap()
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
        val userEmail = firebaseAuth.currentUser?.email!!
        val orderRef = firestore.collection("users").document(userEmail).collection("orders")

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
        val orderRef = firestore.collection("users").document(userEmail).collection("orders")

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