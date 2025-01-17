package org.nullgroup.lados.data.repositories.implementations.coupon

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.CustomerCoupon
import org.nullgroup.lados.data.models.ServerCoupon
import org.nullgroup.lados.data.repositories.interfaces.coupon.CouponRepository

class CouponRepositoryImplement(
    private val firestore: FirebaseFirestore,
): CouponRepository {
    private val userCollectionName = "users"
    private val serverCouponCollectionName = "serverCoupons"
    private val userCouponCollectionName = "customerCoupons"

    // Including clean up of unused coupons
    override suspend fun getCouponsForCustomer(customerId: String): Result<List<CustomerCoupon>> =
        coroutineScope {
            try {
                val customerCouponRef = firestore
                    .collection(userCollectionName)
                    .document(customerId)
                    .collection(userCouponCollectionName)
                val serverCouponRef = firestore.collection(serverCouponCollectionName)

                val customerCoupons = customerCouponRef.get().await().documents.mapNotNull {
                    it.toObject(CustomerCoupon::class.java)
                }

                val usableCoupons: MutableList<CustomerCoupon> = mutableListOf()

                val fetchOwningCoupons = async {
                    customerCoupons.forEach { coupon ->
                        if (coupon.eligibleForUsage()) {
                            usableCoupons.add(coupon)
                        }
                        val serverCoupon = serverCouponRef
                            .whereEqualTo("code", coupon.code)
                            .get()
                            .await()
                            .firstOrNull()
                            ?.toObject(ServerCoupon::class.java)
                        if (coupon.eligibleForCleanup(serverCoupon)) {
                            // Clean up the unlisted coupon
                            customerCouponRef.document(coupon.id).delete()
                        }
                    }
                }

                val fetchGlobalCoupons = async {
                    val globalCoupons = serverCouponRef
                        .whereEqualTo("autoFetching", true)
                        .whereGreaterThan("endDate", Timestamp.now())
                        .get().await()
                        .documents.mapNotNull { it.toObject(ServerCoupon::class.java) }
                    globalCoupons.forEach { globalCoupon ->
                        val redeemedCoupon = customerCoupons.firstOrNull { it.code == globalCoupon.code }
                        if (redeemedCoupon == null) {
                            // Add the global coupon to the customer's account

                            val userCoupon = CustomerCoupon.checkAndCreateFrom(globalCoupon)
                            if (userCoupon is CustomerCoupon.Companion.CouponRedemptionResult.Success) {
                                val newCouponRef = customerCouponRef.document()
                                newCouponRef.set(userCoupon.coupon)
                                usableCoupons.add(userCoupon.coupon.copy(id = newCouponRef.id))
                            } else {
                                Log.e("CouponRepositoryImplement", "Not adding global coupon to user: ${
                                    (userCoupon as CustomerCoupon.Companion.CouponRedemptionResult.Error).error.name
                                }")
                            }
                        }
                    }
                }

                awaitAll(fetchOwningCoupons, fetchGlobalCoupons)

                return@coroutineScope Result.success(usableCoupons)
            } catch (e: Exception) {
                return@coroutineScope Result.failure(e)
            }
        }

    suspend fun redeemCouponForCustomer(customerId: String, couponCode: String) {

    }

    fun changeCouponUsageStatus(customerId: String, couponId: String, isUsed: Boolean) {

    }

    override suspend fun addCouponToServer(coupon: ServerCoupon): Result<Boolean> {
        return try {
            firestore
                .collection(serverCouponCollectionName)
                .document()
                .set(coupon)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CouponRepositoryImplement", "Error adding coupon to server: ", e)
            Result.failure(e)
        }
    }
}