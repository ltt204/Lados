package org.nullgroup.lados.data.models

import android.icu.util.TimeZone
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun currentHostTimeZone() = TimeZone.getDefault()

fun currentHostTimeZoneInString() = currentHostTimeZone().id

fun LocalDateTime.toTimestamp(zoneId: String): Timestamp {
    val zonedDateTime = ZonedDateTime.of(this, ZoneId.of(zoneId))
    return Timestamp(zonedDateTime.toInstant().epochSecond, 0)
}

fun Timestamp.toLocalDateTime(zoneId: String): LocalDateTime {
    val zonedDateTime = ZonedDateTime.ofInstant(this.toDate().toInstant(), ZoneId.of(zoneId))
    return zonedDateTime.toLocalDateTime()
}

fun timestampFromNow(seconds: Long = 0): Timestamp {
    val currentDate = System.currentTimeMillis()
    val futureDate = currentDate + seconds * 1000
    return Timestamp(futureDate / 1000, 0)
}

fun Long.toDurationInSeconds(): Duration {
    return this.toDuration(DurationUnit.SECONDS)
}

fun Duration.toLongFromSeconds(): Long {
    return this.toLong(DurationUnit.SECONDS)
}

/**
 * @param code Should be trimmed and in uppercase
 * @param startDate The start date of the coupon, inclusive
 * @param endDate (REQUIRED) The end date of the coupon, exclusive
 * @param usageDuration
 * If not null, the coupon will be valid for [usageDuration] seconds after it is redeemed.
 * Otherwise, the coupon will be valid until [endDate] (if specified) or until it is removed from the database.
 * @param autoFetching If true, the coupon will be automatically added to the customer's account,
 * and the following fields will be ignored when adding this way: [usageDuration], [maximumRedemption]
 */
data class ServerCoupon(
    @DocumentId val id: String = "",
    val code: String = "",
    val discountPercentage: Int = 0,
    val maximumDiscount: Double? = null,
    val minimumOrderAmount: Double = 0.0,
    val startDate: Timestamp = timestampFromNow(), // inclusive
    val endDate: Timestamp = timestampFromNow(DEFAULT_DATE_DIFF_IN_SECOND), // exclusive
    val usageDuration: Long? = DEFAULT_USAGE_DURATION_IN_SECOND, // in seconds
    val redeemedCount: Int = 0,
    val maximumRedemption: Int? = null,
    val autoFetching: Boolean = false,
        // If true, the coupon will be automatically added to the customer's account
        // [usageDuration] will be ignored (the coupon will be valid until [endDate] (if specified)
        // or until it is removed from the database)

) {
    init {
//        require(code.isNotBlank()) { "Code must not be blank" }
//        require(discountPercentage in 0..100) { "Discount percentage must be between 0 and 100" }
//        require(minimumOrderAmount >= 0) { "Minimum order amount must be greater than or equal to 0" }
//        require(startDate.seconds < endDate.seconds) { "Start date must be before end date" }
//        require(usageDuration == null || usageDuration > 0) { "Usage duration must be greater than 0" }
//        require(maximumRedemption == null || maximumRedemption >= 0) { "Maximum redemption must be greater than or equal to 0" }
    }

    companion object {
        /**
         * The default date difference in seconds between the start and end date
         * for the coupon to be valid.
         * Equivalent to 7 days
         */
        const val DEFAULT_DATE_DIFF_IN_SECOND: Long = 60 * 60 * 24 * 7 + 1

        /**
         * The default usage duration in seconds for the coupon to be valid
         * Equivalent to 1 day
         */
        const val DEFAULT_USAGE_DURATION_IN_SECOND: Long = 60 * 60 * 24 + 1

        enum class CouponValidationError {
            EMPTY_CODE,
            OUT_OF_RANGE_DISCOUNT_PERCENTAGE,
            NEGATIVE_MAXIMUM_DISCOUNT,
            NEGATIVE_MINIMUM_ORDER_AMOUNT,
            END_DATE_BEFORE_START_DATE,
            END_DATE_BEFORE_CURRENT_DATE,
            NON_POSITIVE_USAGE_DURATION,
            NEGATIVE_MAXIMUM_REDEMPTION,
        }
    }

}

/**
 * Once redeemed, any changes to the server coupon will not affect the customer coupon
 * unless the corresponding server coupon is removed from the database.
 */
data class CustomerCoupon(
    @DocumentId val id: String = "",
    val code: String = "",
    val discountPercentage: Int = 0,
    val maximumDiscount: Double? = null,
    val minimumOrderAmount: Double = 0.0,
    val effectiveAt: Timestamp = timestampFromNow(),
    val expiredAt: Timestamp = timestampFromNow(DEFAULT_DATE_DIFF_IN_SECOND),
    val hasBeenUsed: Boolean = false,
        // Can't be named "isUsed" as Kotlin treats it as a boolean getter when serializing,
        // which creates a field named "used" in the database instead of "isUsed"
) {
    companion object {
        /**
         * The default date difference in seconds for the coupon to be valid. Equivalent to 1 day
         */
        const val DEFAULT_DATE_DIFF_IN_SECOND: Long = 60 * 60 * 24 * 1

        /**
         * The default keep time after the coupon is expired in seconds, before it is removed
         * from the database.
         * Equivalent to 7 days
         */
        const val DEFAULT_KEEP_TIME_AFTER_EXPIRED_IN_SECOND: Long = 60 * 60 * 24 * 7


        enum class CouponRedemptionError {
            UNAVAILABLE_COUPON, // The code is not matched with any available coupons
            EXCEED_MAXIMUM_REDEMPTION, // The coupon has been redeemed more than the maximum redemption
            EXPIRED_COUPON, // The coupon is redeemed after the end date
            ALREADY_REDEEMED_CODE, // The code has been redeemed
            INTERNAL_ERROR, // An internal error occurred while redeeming the coupon
        }

        sealed class CouponRedemptionResult {
            data class Success(val coupon: CustomerCoupon) : CouponRedemptionResult()
            data class Error(val error: CouponRedemptionError) : CouponRedemptionResult()
        }


        enum class CouponUsageError {
            COUPON_NOT_EFFECTIVE_YET, // The coupon is not effective yet
            MINIMUM_ORDER_AMOUNT_NOT_REACHED, // The total amount is less than the minimum order amount
            COUPON_EXPIRED, // The coupon is expired
            COUPON_ALREADY_USED, // The coupon has been used
        }

        sealed class CouponUsageResult {
            data class Success(val discountAmount: Double) : CouponUsageResult()
            data class Error(val error: CouponUsageError) : CouponUsageResult()
        }

        /**
         * Convert a [ServerCoupon] to a [CustomerCoupon], without checking if the coupon is valid.
         * Should not be called directly, use [checkAndCreateFrom] instead!
         */
        private fun from(serverCoupon: ServerCoupon): CustomerCoupon {
            return CustomerCoupon(
                code = serverCoupon.code,
                discountPercentage = serverCoupon.discountPercentage,
                maximumDiscount = serverCoupon.maximumDiscount,
                minimumOrderAmount = serverCoupon.minimumOrderAmount,
                effectiveAt = timestampFromNow(),
                expiredAt = if (!serverCoupon.autoFetching && serverCoupon.usageDuration != null) {
                    timestampFromNow(serverCoupon.usageDuration)
                } else {
                    serverCoupon.endDate
                },
            )
        }

        /**
         * Check if the [serverCoupon] is valid and create a [CustomerCoupon] from it
         */
        internal fun checkAndCreateFrom(serverCoupon: ServerCoupon?): CouponRedemptionResult {
            if (serverCoupon == null) {
                return CouponRedemptionResult.Error(CouponRedemptionError.UNAVAILABLE_COUPON)
            }
            val currentTimeMillis = System.currentTimeMillis()

            // autoFetching coupons are not checked for maximum redemption
            if (!serverCoupon.autoFetching) {
                if (serverCoupon.redeemedCount >= (serverCoupon.maximumRedemption
                        ?: Int.MAX_VALUE)
                ) {
                    return CouponRedemptionResult.Error(CouponRedemptionError.EXCEED_MAXIMUM_REDEMPTION)
                }
            }
            if (currentTimeMillis >= serverCoupon.endDate.seconds * 1000) {
                return CouponRedemptionResult.Error(CouponRedemptionError.EXPIRED_COUPON)
            }
            return CouponRedemptionResult.Success(from(serverCoupon))
        }
    }

    fun checkAndCalculateForDiscount(totalAmount: Double): CouponUsageResult {
        if (this@CustomerCoupon.hasBeenUsed) {
            return CouponUsageResult.Error(CouponUsageError.COUPON_ALREADY_USED)
        }
        val currentDate = System.currentTimeMillis()
        if (currentDate < effectiveAt.seconds * 1000) {
            return CouponUsageResult.Error(CouponUsageError.COUPON_NOT_EFFECTIVE_YET)
        }
        if (currentDate >= expiredAt.seconds * 1000) {
            return CouponUsageResult.Error(CouponUsageError.COUPON_EXPIRED)
        }
        if (totalAmount < minimumOrderAmount) {
            return CouponUsageResult.Error(CouponUsageError.MINIMUM_ORDER_AMOUNT_NOT_REACHED)
        }
        val discountAmount = totalAmount * discountPercentage.toDouble() / 100.0
        return if (maximumDiscount != null) {
            if (discountAmount > maximumDiscount) {
                CouponUsageResult.Success(maximumDiscount)
            } else {
                CouponUsageResult.Success(discountAmount)
            }
        } else {
            CouponUsageResult.Success(discountAmount)
        }
    }

    fun discountAmountFrom(totalAmount: Double): Double {
        return checkAndCalculateForDiscount(totalAmount).let {
            if (it is CouponUsageResult.Success) {
                it.discountAmount
            } else {
                0.0
            }
        }
    }

    /**
     * [eligibleForCleanup] should be called before [eligibleForUsage]
     */
    internal fun eligibleForCleanup(serverCoupon: ServerCoupon?): Boolean {
        if (serverCoupon == null) {
            return true
        }

        val currentDate = System.currentTimeMillis()
        return currentDate >= expiredAt.seconds * 1000 + DEFAULT_KEEP_TIME_AFTER_EXPIRED_IN_SECOND * 1000
    }

    internal fun eligibleForUsage(): Boolean {
        val currentDate = System.currentTimeMillis()
        return !this@CustomerCoupon.hasBeenUsed && currentDate < expiredAt.seconds * 1000
    }
}