package org.nullgroup.lados.viewmodels.admin.coupon

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.nullgroup.lados.data.models.ServerCoupon
import org.nullgroup.lados.utilities.datetime.secondsFrom
import org.nullgroup.lados.utilities.datetime.timestampFrom
import org.nullgroup.lados.utilities.datetime.timestampFromNow
import org.nullgroup.lados.utilities.datetime.toDateMillis
import org.nullgroup.lados.utilities.datetime.toDayCountsFromSeconds
import org.nullgroup.lados.utilities.datetime.toEpochMillis
import org.nullgroup.lados.utilities.datetime.toTimeOfDayInSecondsFromSeconds
import org.nullgroup.lados.utilities.datetime.toTimeOfDayMillis
import org.nullgroup.lados.utilities.text.fromTextFieldStringAsDouble
import org.nullgroup.lados.utilities.text.fromTextFieldStringAsNullableDouble
import java.time.Instant
import javax.inject.Inject

typealias CouponValidationError = ServerCoupon.Companion.CouponValidationError

data class FieldInfo<T>(
    val value: T,
    var errors: List<CouponValidationError> = emptyList(),
    val markedAsNull: Boolean = false,
    val preservingDecimalPoint: Boolean = false,
) {
    val isError: Boolean get() = errors.isNotEmpty()
//    inline fun <reified T> isValueNullable(): Boolean = typeOf<T>().isMarkedNullable
//
//    inline fun <reified T> updateNullable(
//        newValue: T? = null,
//        newErrors: List<CouponValidationError>? = null
//    ): FieldInfo<T?> {
//        if (isValueNullable<T>()) {
//            return FieldInfo<T?>(
//                value = newValue,
//                errors = newErrors ?: this.errors,
//                markedAsNull = markedAsNull,
//            )
//        }
//        throw IllegalStateException("FieldInfo value is not nullable")
//    }

    fun updateWithDirectValue(
        newValue: T,
        newErrors: List<CouponValidationError>? = null,
        newMarkedAsNull: Boolean? = null,
        newPreservingDecimalPoint: Boolean? = null,
    ): FieldInfo<T> {
        return FieldInfo(
            value = newValue,
            errors = newErrors ?: errors,
            markedAsNull = newMarkedAsNull ?: markedAsNull,
            preservingDecimalPoint = newPreservingDecimalPoint ?: preservingDecimalPoint,
        )
    }

    fun update(
        newValue: T? = null,
        newErrors: List<CouponValidationError>? = null,
        newMarkedAsNull: Boolean? = null,
        newPreservingDecimalPoint: Boolean? = null,
    ): FieldInfo<T> {
        return FieldInfo(
            value = newValue ?: value,
            errors = newErrors ?: errors,
            markedAsNull = newMarkedAsNull ?: markedAsNull,
            preservingDecimalPoint = newPreservingDecimalPoint ?: preservingDecimalPoint,
        )
    }
}

data class CouponFormUiState(
    val code: FieldInfo<String> = FieldInfo("NEW_CODE"),
    val discountPercentage: FieldInfo<Int> = FieldInfo(10),
    val maximumDiscount: FieldInfo<Double?> = FieldInfo(null),
    val minimumOrderAmount: FieldInfo<Double> = FieldInfo(0.0),
    val startDate: FieldInfo<Timestamp> = FieldInfo(timestampFromNow()),
    val endDate: FieldInfo<Timestamp> = FieldInfo(
        timestampFromNow(ServerCoupon.DEFAULT_DATE_DIFF_IN_SECOND)
    ),
    val usageDuration: FieldInfo<Long?> = FieldInfo(null),
    val maximumRedemption: FieldInfo<Int?> = FieldInfo(null),
    val autoFetching: FieldInfo<Boolean> = FieldInfo(false),

    val dateZoneId: String = "UTC",
) {
    companion object {
        fun fromServerCoupon(serverCoupon: ServerCoupon, zoneId: String): CouponFormUiState {
            return CouponFormUiState(
                code = FieldInfo(serverCoupon.code),
                discountPercentage = FieldInfo(serverCoupon.discountPercentage),
                maximumDiscount = FieldInfo(serverCoupon.maximumDiscount),
                startDate = FieldInfo(serverCoupon.startDate),
                endDate = FieldInfo(serverCoupon.endDate),
                usageDuration = FieldInfo(serverCoupon.usageDuration),
                maximumRedemption = FieldInfo(serverCoupon.maximumRedemption),
                autoFetching = FieldInfo(serverCoupon.autoFetching),
                dateZoneId = zoneId,
            )
        }
    }

    fun isValid(): Boolean {
        return code.errors.isEmpty() &&
                discountPercentage.errors.isEmpty() &&
                maximumDiscount.errors.isEmpty() &&
                minimumOrderAmount.errors.isEmpty() &&
                startDate.errors.isEmpty() &&
                endDate.errors.isEmpty() &&
                usageDuration.errors.isEmpty() &&
                maximumRedemption.errors.isEmpty() &&
                autoFetching.errors.isEmpty()
    }
}

sealed class CouponFormEvent {
    data class CodeChanged(val code: String) : CouponFormEvent()
    data class DiscountPercentageChanged(val discountPercentage: String) : CouponFormEvent()
    data class MaximumDiscountChanged(val maximumDiscount: String) : CouponFormEvent()
    data class MinimumOrderAmountChanged(val minimumOrderAmount: String) : CouponFormEvent()
    data class StartDateChanged(val utcDateMillis: Long?, val utcTimeMillis: Long?) :
        CouponFormEvent()

    data class EndDateChanged(val utcDateMillis: Long?, val utcTimeMillis: Long?) :
        CouponFormEvent()

    data class UsageDurationChanged(val dayCount: String?, val timeOfDayInSecond: Long?) :
        CouponFormEvent()

    data class MaximumRedemptionChanged(val maximumRedemption: String) : CouponFormEvent()
    data class AutoFetchingChanged(val autoFetching: Boolean) : CouponFormEvent()

    object MaximumDiscountNullMarkChanged : CouponFormEvent()
    object UsageDurationNullMarkChanged : CouponFormEvent()
    object MaximumRedemptionNullMarkChanged : CouponFormEvent()

    data class Submit(
        val onApproved: ((ServerCoupon) -> Unit)? = null,
        val onRejected: (() -> Unit)? = null,
    ) : CouponFormEvent()
}

@HiltViewModel
class CouponFormViewModel @Inject constructor(
) : ViewModel() {
    private var _couponFormUiState = MutableStateFlow(CouponFormUiState())
    val couponFormUiState = _couponFormUiState.asStateFlow()

    fun initialize(coupon: ServerCoupon?, zoneId: String) {
        if (coupon == null) {
            _couponFormUiState.value = CouponFormUiState()
        } else {
            _couponFormUiState.value = CouponFormUiState.fromServerCoupon(coupon, zoneId)
        }
    }

    fun handleEvent(event: CouponFormEvent) {
        when (event) {
            is CouponFormEvent.CodeChanged -> updateCode(event.code)
            is CouponFormEvent.DiscountPercentageChanged -> updateDiscountPercentage(event.discountPercentage)
            is CouponFormEvent.MaximumDiscountChanged -> updateMaximumDiscount(event.maximumDiscount)
            is CouponFormEvent.MinimumOrderAmountChanged -> updateMinimumOrderAmount(event.minimumOrderAmount)
            is CouponFormEvent.StartDateChanged -> updateStartDate(
                event.utcDateMillis,
                event.utcTimeMillis
            )

            is CouponFormEvent.EndDateChanged -> updateEndDate(
                event.utcDateMillis,
                event.utcTimeMillis
            )

            is CouponFormEvent.UsageDurationChanged -> updateUsageDuration(
                event.dayCount,
                event.timeOfDayInSecond
            )

            is CouponFormEvent.MaximumRedemptionChanged -> updateMaximumRedemption(event.maximumRedemption)
            is CouponFormEvent.AutoFetchingChanged -> updateAutoFetching(event.autoFetching)
            is CouponFormEvent.MaximumDiscountNullMarkChanged -> updateMaximumDiscountNullMark()
            is CouponFormEvent.UsageDurationNullMarkChanged -> updateUsageDurationNullMark()
            is CouponFormEvent.MaximumRedemptionNullMarkChanged -> updateMaximumRedemptionNullMark()
            is CouponFormEvent.Submit -> submitCoupon(event.onApproved)
        }
    }

    private fun submitCoupon(
        onApproved: ((ServerCoupon) -> Unit)? = null,
        onRejected: (() -> Unit)? = null,
    ) {
        if (couponFormUiState.value.isValid()) {
            onApproved?.invoke(
                ServerCoupon(
                    code = couponFormUiState.value.code.value,
                    discountPercentage = couponFormUiState.value.discountPercentage.value,
                    maximumDiscount = if (couponFormUiState.value.maximumDiscount.markedAsNull) null
                    else couponFormUiState.value.maximumDiscount.value,
                    minimumOrderAmount = couponFormUiState.value.minimumOrderAmount.value,
                    startDate = couponFormUiState.value.startDate.value,
                    endDate = couponFormUiState.value.endDate.value,
                    usageDuration = if (couponFormUiState.value.usageDuration.markedAsNull) null
                    else couponFormUiState.value.usageDuration.value,
                    maximumRedemption = if (couponFormUiState.value.maximumRedemption.markedAsNull)
                        couponFormUiState.value.maximumRedemption.value
                    else null,
                    autoFetching = couponFormUiState.value.autoFetching.value,
                )
            )
        } else {
            onRejected?.invoke()
        }
    }

    private fun updateCode(code: String) {
        val errors = mutableListOf<CouponValidationError>()
        val code = code.trim()

        if (code.isEmpty()) {
            errors.add(CouponValidationError.EMPTY_CODE)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            code = _couponFormUiState.value.code.update(
                newValue = code.uppercase(),
                newErrors = errors,
            ),
        )
    }

    private fun updateDiscountPercentage(discountPercentage: String) {
        val discountPercentage = discountPercentage.toIntOrNull() ?: return

        val errors = mutableListOf<CouponValidationError>()

        if ((discountPercentage in 0..100).not()) {
            errors.add(CouponValidationError.OUT_OF_RANGE_DISCOUNT_PERCENTAGE)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            discountPercentage = _couponFormUiState.value.discountPercentage.update(
                newValue = discountPercentage,
                newErrors = errors,
            ),
        )
    }

    private fun updateMaximumDiscount(maximumDiscount: String) {
        var isPreservingDecimalPoint = false
        val maximumDiscount = maximumDiscount.fromTextFieldStringAsNullableDouble {
            isPreservingDecimalPoint = it
        }
        if (maximumDiscount == null) {
            _couponFormUiState.value = _couponFormUiState.value.copy(
                maximumDiscount = _couponFormUiState.value.maximumDiscount.updateWithDirectValue(
                    newValue = null,
                    newErrors = emptyList(),
                    newPreservingDecimalPoint = isPreservingDecimalPoint,
                ),
            )
            return
        } else if (maximumDiscount.isNaN()) {
            return
        }

        val errors = mutableListOf<CouponValidationError>()

        if (maximumDiscount < 0) {
            errors.add(CouponValidationError.NEGATIVE_MAXIMUM_DISCOUNT)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            maximumDiscount = _couponFormUiState.value.maximumDiscount.update(
                newValue = maximumDiscount,
                newErrors = errors,
                newPreservingDecimalPoint = isPreservingDecimalPoint,
            ),
        )
    }

    private fun updateMinimumOrderAmount(minimumOrderAmount: String) {
        var isPreservingDecimalPoint = false
        val minimumOrderAmount = minimumOrderAmount.fromTextFieldStringAsDouble {
            isPreservingDecimalPoint = it
        }
        if (minimumOrderAmount.isNaN()) {
            return
        }

        val errors = mutableListOf<CouponValidationError>()

        if (minimumOrderAmount < 0) {
            errors.add(CouponValidationError.NEGATIVE_MINIMUM_ORDER_AMOUNT)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            minimumOrderAmount = _couponFormUiState.value.minimumOrderAmount.update(
                newValue = minimumOrderAmount,
                newErrors = errors,
                newPreservingDecimalPoint = isPreservingDecimalPoint,
            ),
        )
    }

    private fun updateStartDate(utcDateMillis: Long?, utcTimeMillis: Long?) {
        if (utcDateMillis == null && utcTimeMillis == null) {
            return
        }

        val previousStartDate = _couponFormUiState.value.startDate.value
        _couponFormUiState.value = _couponFormUiState.value.copy(
            startDate = _couponFormUiState.value.startDate.update(
                newValue = timestampFrom(
                    dateMillis = utcDateMillis ?: previousStartDate.toDateMillis(),
                    timeOfDayMillis = utcTimeMillis ?: previousStartDate.toTimeOfDayMillis(),
                ),
            ),
        )
    }

    private fun updateEndDate(utcDateMillis: Long?, utcTimeMillis: Long?) {
        if (utcDateMillis == null && utcTimeMillis == null) {
            return
        }
        val previousStartDate = _couponFormUiState.value.startDate.value
        val previousEndDate = _couponFormUiState.value.endDate.value
        val endDate = timestampFrom(
            dateMillis = utcDateMillis ?: previousEndDate.toDateMillis(),
            timeOfDayMillis = utcTimeMillis ?: previousEndDate.toTimeOfDayMillis(),
        )

        val errors = mutableListOf<CouponValidationError>()

        if (endDate.toEpochMillis() < Instant.now().toEpochMilli()) {
            errors.add(CouponValidationError.END_DATE_BEFORE_CURRENT_DATE)
        }

        if (endDate.toEpochMillis() < previousStartDate.toEpochMillis()) {
            errors.add(CouponValidationError.END_DATE_BEFORE_START_DATE)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            endDate = _couponFormUiState.value.endDate.update(
                newValue = endDate,
                newErrors = errors,
            ),
        )
    }

    private fun updateUsageDuration(dayCount: String?, timeOfDayInSecond: Long?) {
        if (dayCount == null && timeOfDayInSecond == null) {
            return
        }
        if (timeOfDayInSecond == null && dayCount?.trim()?.isEmpty() == true) {
            _couponFormUiState.value = _couponFormUiState.value.copy(
                usageDuration = _couponFormUiState.value.usageDuration.updateWithDirectValue(
                    newValue = null,
                    newErrors = emptyList(),
                ),
            )
            return
        }
        val previousDayCount =
            _couponFormUiState.value.usageDuration.value?.toDayCountsFromSeconds() ?: 0
        val previousTimeOfDayInSecond =
            _couponFormUiState.value.usageDuration.value?.toTimeOfDayInSecondsFromSeconds() ?: 0
        val usageDuration = secondsFrom(
            dayCounts = if (dayCount?.isEmpty() == true) {
                0
            } else {
                dayCount?.toLongOrNull() ?: previousDayCount
            },
            timeOfDayInSecond = timeOfDayInSecond ?: previousTimeOfDayInSecond,
        )
        val errors = mutableListOf<CouponValidationError>()

        if (usageDuration <= 0) {
            errors.add(CouponValidationError.NON_POSITIVE_USAGE_DURATION)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            usageDuration = _couponFormUiState.value.usageDuration.update(
                newValue = usageDuration,
                newErrors = errors,
            ),
        )
    }

    private fun updateMaximumRedemption(maximumRedemption: String) {
//        if (maximumRedemption.isEmpty()) {
//            try {
//                _couponFormUiState.value = _couponFormUiState.value.copy(
//                    maximumRedemption = _couponFormUiState.value.maximumRedemption.updateNullable(
//                        newValue = null,
//                        newErrors = emptyList(),
//                    ),
//                )
//            } catch (_: Exception) {
//
//            }
//            return
//        }

        if (maximumRedemption.isEmpty()) {
            _couponFormUiState.value = _couponFormUiState.value.copy(
                maximumRedemption = _couponFormUiState.value.maximumRedemption.updateWithDirectValue(
                    newValue = null,
                    newErrors = emptyList(),
                ),
            )
            return
        }

        val maximumRedemption = maximumRedemption.toIntOrNull() ?: return
        val errors = mutableListOf<CouponValidationError>()

        if (maximumRedemption < 0) {
            errors.add(CouponValidationError.NEGATIVE_MAXIMUM_REDEMPTION)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            maximumRedemption = _couponFormUiState.value.maximumRedemption.update(
                newValue = maximumRedemption,
                newErrors = errors,
            ),
        )
    }

    private fun updateAutoFetching(autoFetching: Boolean) {
        _couponFormUiState.value = _couponFormUiState.value.copy(
            autoFetching = _couponFormUiState.value.autoFetching.update(
                newValue = autoFetching,
            ),

            usageDuration = if (autoFetching) {
                _couponFormUiState.value.usageDuration.update(
                    newMarkedAsNull = true,
                )
            } else {
                _couponFormUiState.value.usageDuration
            },

            maximumRedemption = if (autoFetching) {
                _couponFormUiState.value.maximumRedemption.update(
                    newMarkedAsNull = true,
                )
            } else {
                _couponFormUiState.value.maximumRedemption
            },
        )
    }

    private fun updateMaximumDiscountNullMark() {
        _couponFormUiState.value = _couponFormUiState.value.copy(
            maximumDiscount = _couponFormUiState.value.maximumDiscount.update(
                newMarkedAsNull = !_couponFormUiState.value.maximumDiscount.markedAsNull,
            ),
        )
    }

    private fun updateUsageDurationNullMark() {
        _couponFormUiState.value = _couponFormUiState.value.copy(
            usageDuration = _couponFormUiState.value.usageDuration.update(
                newMarkedAsNull = !_couponFormUiState.value.usageDuration.markedAsNull,
            ),
        )
    }

    private fun updateMaximumRedemptionNullMark() {
        _couponFormUiState.value = _couponFormUiState.value.copy(
            maximumRedemption = _couponFormUiState.value.maximumRedemption.update(
                newMarkedAsNull = !_couponFormUiState.value.maximumRedemption.markedAsNull,
            ),
        )
    }
}