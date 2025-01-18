package org.nullgroup.lados.data.repositories.implementations.coupon

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
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
) : CouponRepository {
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
                        val serverCoupon = serverCouponRef
                            .whereEqualTo("code", coupon.code)
                            .get().await()
                            .documents.firstOrNull()
                            ?.toObject(ServerCoupon::class.java)
                        if (coupon.eligibleForCleanup(serverCoupon)) {
                            // Clean up the unlisted coupon
                            customerCouponRef.document(coupon.id).delete()
                        } else if (coupon.eligibleForUsage()) {
                            usableCoupons.add(coupon)
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
                        val redeemedCoupon =
                            customerCoupons.firstOrNull { it.code == globalCoupon.code }
                        if (redeemedCoupon == null) {
                            // Add the global coupon to the customer's account

                            val userCoupon = CustomerCoupon.checkAndCreateFrom(globalCoupon)
                            if (userCoupon is CustomerCoupon.Companion.CouponRedemptionResult.Success) {
                                val newCouponRef = customerCouponRef.document()
                                newCouponRef.set(userCoupon.coupon)
                                usableCoupons.add(userCoupon.coupon.copy(id = newCouponRef.id))
                            } else {
                                Log.e(
                                    "CouponRepositoryImplement",
                                    "Not adding global coupon to user: ${
                                        (userCoupon as CustomerCoupon.Companion.CouponRedemptionResult.Error).error.name
                                    }"
                                )
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

    override suspend fun redeemCoupon(
        customerId: String,
        couponCode: String
    ): Result<CustomerCoupon.Companion.CouponRedemptionResult> {
        return try {
            val couponCode = couponCode.trim().uppercase()

            val customerCouponRef = firestore
                .collection(userCollectionName)
                .document(customerId)
                .collection(userCouponCollectionName)
            val serverCouponsRef = firestore.collection(serverCouponCollectionName)

            val matchingServerCouponRef = serverCouponsRef
                .whereEqualTo("code", couponCode)
                .get().await()
                .documents.firstOrNull()
            if (matchingServerCouponRef == null) {
                return Result.success(
                    CustomerCoupon.Companion.CouponRedemptionResult.Error(
                        CustomerCoupon.Companion.CouponRedemptionError.UNAVAILABLE_COUPON
                    )
                )
            }

            val existingCouponQuery = customerCouponRef
                .whereEqualTo("code", couponCode)
                .get().await()
            if (existingCouponQuery.isEmpty.not()) {
                return Result.success(
                    CustomerCoupon.Companion.CouponRedemptionResult.Error(
                        CustomerCoupon.Companion.CouponRedemptionError.ALREADY_REDEEMED_CODE
                    )
                )
            }

            firestore.runTransaction { transaction ->
                val serverCoupon = transaction
                    .get(matchingServerCouponRef.reference)
                    .toObject(ServerCoupon::class.java)!!
                if (serverCoupon.redeemedCount >= (serverCoupon.maximumRedemption
                        ?: Int.MAX_VALUE)
                ) {
                    return@runTransaction CustomerCoupon.Companion.CouponRedemptionResult.Error(
                        CustomerCoupon.Companion.CouponRedemptionError.EXCEED_MAXIMUM_REDEMPTION
                    )
                }

                val redemptionResult = CustomerCoupon.checkAndCreateFrom(serverCoupon)
                if (redemptionResult is CustomerCoupon.Companion.CouponRedemptionResult.Success) {
                    val newCouponRef = customerCouponRef.document()
                    transaction.set(newCouponRef, redemptionResult.coupon)

                    transaction.update(
                        matchingServerCouponRef.reference,
                        "redeemedCount",
                        FieldValue.increment(1)
                    )
                    return@runTransaction redemptionResult.copy(
                        coupon = redemptionResult.coupon.copy(id = newCouponRef.id)
                    )

                }
                return@runTransaction redemptionResult
            }.await()
                .let {
                    Result.success(it)
                }.onFailure { e: Throwable ->
                    Log.e("CouponRepositoryImplement", "Error redeeming coupon: ", e)
                    Result.failure<CustomerCoupon.Companion.CouponRedemptionResult>(e)
                }
        } catch (e: Exception) {
            Log.e("CouponRepositoryImplement", "Error redeeming coupon for customer: ", e)
            return Result.failure(e)
        }
    }

    // TODO - Technical Debt: Transaction between order creation and coupon change may be not atomic
    override suspend fun updateCouponUsageStatus(
        customerId: String,
        couponId: String,
        isUsed: Boolean,
    ): Result<Boolean> {
        return try {
            firestore
                .collection(userCollectionName)
                .document(customerId)
                .collection(userCouponCollectionName)
                .document(couponId)
                .update("hasBeenUsed", isUsed)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CouponRepositoryImplement", "Error changing coupon usage status: ", e)
            Result.failure(e)
        }
    }

    override suspend fun addCouponToServer(coupon: ServerCoupon): Result<Boolean> {
        return try {
            firestore
                .collection(serverCouponCollectionName)
                .document()
                .set(coupon.copy(code = coupon.code.trim().uppercase()))
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("CouponRepositoryImplement", "Error adding coupon to server: ", e)
            Result.failure(e)
        }
    }
}