package org.nullgroup.lados.viewmodels.admin.coupon

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.nullgroup.lados.compose.cart.DialogInfo
import org.nullgroup.lados.data.models.ServerCoupon
import org.nullgroup.lados.data.models.toLocalDateTime
import java.time.LocalDateTime
import javax.inject.Inject

typealias CouponValidationError = ServerCoupon.Companion.CouponValidationError

data class FieldInfo<T>(
    val value: T,
    var errors: List<CouponValidationError> = emptyList(),
    val markedAsNull: Boolean = false
) {
    val isError: Boolean get() = errors.isNotEmpty()

    fun update(
        newValue: T? = null,
        newErrors: List<CouponValidationError>? = null,
        newMarkedAsNull: Boolean? = null,
    ): FieldInfo<T> {
        return FieldInfo(
            value = newValue ?: value,
            errors = newErrors ?: errors,
            markedAsNull = newMarkedAsNull ?: markedAsNull,
        )
    }
}

data class CouponFormUiState(
    val code: FieldInfo<String> = FieldInfo(""),
    val discountPercentage: FieldInfo<Int> = FieldInfo(10),
    val maximumDiscount: FieldInfo<Double?> = FieldInfo(null),
    val minimumOrderAmount: FieldInfo<Double> = FieldInfo(0.0),
    val startDate: FieldInfo<LocalDateTime> = FieldInfo(LocalDateTime.now()),
    val endDate: FieldInfo<LocalDateTime> = FieldInfo(
        LocalDateTime.now().plusSeconds(ServerCoupon.DEFAULT_DATE_DIFF_IN_SECOND)
    ),
    val usageDuration: FieldInfo<Long?> = FieldInfo(null),
    val maximumRedemption: FieldInfo<Int?> = FieldInfo(null),
    val autoFetching: FieldInfo<Boolean> = FieldInfo(false),

    val isProcessing: Boolean = false,
    val dialogInfo: DialogInfo? = null,
) {
    companion object {
        fun fromServerCoupon(serverCoupon: ServerCoupon, zoneId: String): CouponFormUiState {
            return CouponFormUiState(
                code = FieldInfo(serverCoupon.code),
                discountPercentage = FieldInfo(serverCoupon.discountPercentage),
                maximumDiscount = FieldInfo(serverCoupon.maximumDiscount),
                startDate = FieldInfo(serverCoupon.startDate.toLocalDateTime(zoneId)),
                endDate = FieldInfo(serverCoupon.endDate.toLocalDateTime(zoneId)),
                usageDuration = FieldInfo(serverCoupon.usageDuration),
                maximumRedemption = FieldInfo(serverCoupon.maximumRedemption),
                autoFetching = FieldInfo(serverCoupon.autoFetching),
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
    data class DiscountPercentageChanged(val discountPercentage: Int) : CouponFormEvent()
    data class MaximumDiscountChanged(val maximumDiscount: Double?) : CouponFormEvent()
    data class MinimumOrderAmountChanged(val minimumOrderAmount: Double) : CouponFormEvent()
    data class StartDateChanged(val startDate: LocalDateTime) : CouponFormEvent()
    data class EndDateChanged(val endDate: LocalDateTime) : CouponFormEvent()
    data class UsageDurationChanged(val usageDuration: Long?) : CouponFormEvent()
    data class MaximumRedemptionChanged(val maximumRedemption: Int?) : CouponFormEvent()
    data class AutoFetchingChanged(val autoFetching: Boolean) : CouponFormEvent()

    object MaximumDiscountNullMarkChanged : CouponFormEvent()
    object UsageDurationNullMarkChanged : CouponFormEvent()
    object MaximumRedemptionNullMarkChanged : CouponFormEvent()

    object Submit : CouponFormEvent()
}

@HiltViewModel
class CouponFormViewModel @Inject constructor(
) : ViewModel() {
    private var _couponFormUiState = MutableStateFlow(CouponFormUiState())
    val couponFormUiState = _couponFormUiState.asStateFlow()

    fun initialize(coupon: ServerCoupon, zoneId: String) {
        _couponFormUiState.value = CouponFormUiState.fromServerCoupon(coupon, zoneId)
    }

    fun handleEvent(event: CouponFormEvent) {
        when (event) {
            is CouponFormEvent.CodeChanged -> updateCode(event.code)
            is CouponFormEvent.DiscountPercentageChanged -> updateDiscountPercentage(event.discountPercentage)
            is CouponFormEvent.MaximumDiscountChanged -> updateMaximumDiscount(event.maximumDiscount)
            is CouponFormEvent.MinimumOrderAmountChanged -> updateMinimumOrderAmount(event.minimumOrderAmount)
            is CouponFormEvent.StartDateChanged -> updateStartDate(event.startDate)
            is CouponFormEvent.EndDateChanged -> updateEndDate(event.endDate)
            is CouponFormEvent.UsageDurationChanged -> updateUsageDuration(event.usageDuration)
            is CouponFormEvent.MaximumRedemptionChanged -> updateMaximumRedemption(event.maximumRedemption)
            is CouponFormEvent.AutoFetchingChanged -> updateAutoFetching(event.autoFetching)
            is CouponFormEvent.MaximumDiscountNullMarkChanged -> updateMaximumDiscountNullMark()
            is CouponFormEvent.UsageDurationNullMarkChanged -> updateUsageDurationNullMark()
            is CouponFormEvent.MaximumRedemptionNullMarkChanged -> updateMaximumRedemptionNullMark()
            is CouponFormEvent.Submit -> submitCoupon()
        }
    }

    private fun submitCoupon() {
        if (couponFormUiState.value.isValid()) {
            _couponFormUiState.value = _couponFormUiState.value.copy(
                isProcessing = true,
            )
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
                newValue = code,
                newErrors = errors,
            ),
        )
    }

    private fun updateDiscountPercentage(discountPercentage: Int) {
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

    private fun updateMaximumDiscount(maximumDiscount: Double?) {
        val errors = mutableListOf<CouponValidationError>()

        if (maximumDiscount != null && maximumDiscount < 0) {
            errors.add(CouponValidationError.NEGATIVE_MAXIMUM_DISCOUNT)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            maximumDiscount = _couponFormUiState.value.maximumDiscount.update(
                newValue = maximumDiscount,
                newErrors = errors,
            ),
        )
    }

    private fun updateMinimumOrderAmount(minimumOrderAmount: Double) {
        val errors = mutableListOf<CouponValidationError>()

        if (minimumOrderAmount < 0) {
            errors.add(CouponValidationError.NEGATIVE_MINIMUM_ORDER_AMOUNT)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            minimumOrderAmount = _couponFormUiState.value.minimumOrderAmount.update(
                newValue = minimumOrderAmount,
                newErrors = errors,
            ),
        )
    }

    private fun updateStartDate(startDate: LocalDateTime) {
        _couponFormUiState.value = _couponFormUiState.value.copy(
            startDate = _couponFormUiState.value.startDate.update(
                newValue = startDate,
            ),
        )
    }

    private fun updateEndDate(endDate: LocalDateTime) {
        val errors = mutableListOf<CouponValidationError>()

        if (endDate.isBefore(LocalDateTime.now())) {
            errors.add(CouponValidationError.END_DATE_BEFORE_CURRENT_DATE)
        }

        if (endDate.isBefore(_couponFormUiState.value.startDate.value)) {
            errors.add(CouponValidationError.END_DATE_BEFORE_START_DATE)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            endDate = _couponFormUiState.value.endDate.update(
                newValue = endDate,
                newErrors = errors,
            ),
        )
    }

    private fun updateUsageDuration(usageDuration: Long?) {
        val errors = mutableListOf<CouponValidationError>()

        if (usageDuration != null && usageDuration <= 0) {
            errors.add(CouponValidationError.NON_POSITIVE_USAGE_DURATION)
        }

        _couponFormUiState.value = _couponFormUiState.value.copy(
            usageDuration = _couponFormUiState.value.usageDuration.update(
                newValue = usageDuration,
                newErrors = errors,
            ),
        )
    }

    private fun updateMaximumRedemption(maximumRedemption: Int?) {
        val errors = mutableListOf<CouponValidationError>()

        if (maximumRedemption != null && maximumRedemption < 0) {
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