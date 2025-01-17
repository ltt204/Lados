package org.nullgroup.lados.data.repositories.interfaces.coupon

import org.nullgroup.lados.data.models.CustomerCoupon
import org.nullgroup.lados.data.models.ServerCoupon

interface CouponRepository {
    suspend fun getCouponsForCustomer(customerId: String): Result<List<CustomerCoupon>>
    suspend fun redeemCoupon(
        customerId: String,
        couponCode: String
    ): Result<CustomerCoupon.Companion.CouponRedemptionResult>

    suspend fun addCouponToServer(coupon: ServerCoupon): Result<Boolean>
}