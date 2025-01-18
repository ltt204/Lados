package org.nullgroup.lados.viewmodels.customer.coupon

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.CustomerCoupon
import org.nullgroup.lados.data.models.ServerCoupon
import org.nullgroup.lados.data.models.currentHostTimeZoneInString
import org.nullgroup.lados.data.models.toTimestamp
import org.nullgroup.lados.data.repositories.interfaces.coupon.CouponRepository
import java.time.LocalDateTime
import javax.inject.Inject

sealed class CouponUiState {
    object Loading : CouponUiState()
    object Empty : CouponUiState()
    data class Success(
        val data: List<CustomerCoupon>,
        val selectedCoupon: CustomerCoupon? = null
    ) : CouponUiState()
    data class Error(val message: String) : CouponUiState()
}

@HiltViewModel
class CouponViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val couponRepository: CouponRepository
) : ViewModel() {
    private val _couponUiState = MutableStateFlow<CouponUiState>(CouponUiState.Loading)
    val couponUiState = _couponUiState.asStateFlow()

    private val customerId = firebaseAuth.currentUser?.uid

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // addCouponToServer()
            fetchCoupons()
        }
    }

    private suspend fun fetchCoupons() {
        customerId?.let { id ->
            couponRepository.getCouponsForCustomer(id).let { result ->
                if (result.isSuccess) {
                    val coupons = result.getOrNull()
                    if (coupons.isNullOrEmpty()) {
                        _couponUiState.value = CouponUiState.Empty
                    } else {
                        _couponUiState.value = CouponUiState.Success(coupons)
                    }
                } else {
                    _couponUiState.value =
                        CouponUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun redeemCoupon(
        code: String,
        onRedeemSuccess: () -> Unit = {},
        onRedeemFailed: (CustomerCoupon.Companion.CouponRedemptionError) -> Unit = {}
    ) {
        if (customerId == null) {
            Log.e("CouponViewModel", "redeemCoupon failed: customerId is null")
            onRedeemFailed(CustomerCoupon.Companion.CouponRedemptionError.INTERNAL_ERROR)
            return
        }

        viewModelScope.launch {
            couponRepository.redeemCoupon(customerId, code).let { result ->
                if (result.isFailure) {
                    Log.e(
                        "CouponViewModel",
                        "redeemCoupon failed: ${result.exceptionOrNull()?.message}"
                    )
                    onRedeemFailed(CustomerCoupon.Companion.CouponRedemptionError.INTERNAL_ERROR)
                    return@launch
                }

                val redemptionResult = result.getOrNull()!!
                when (redemptionResult) {
                    is CustomerCoupon.Companion.CouponRedemptionResult.Error -> {
                        onRedeemFailed(redemptionResult.error)
                    }
                    is CustomerCoupon.Companion.CouponRedemptionResult.Success -> {
                        val newCoupon = redemptionResult.coupon
                        _couponUiState.value = CouponUiState.Success(
                            ((_couponUiState.value as? CouponUiState.Success)?.data ?: emptyList()) + newCoupon,
                            ((_couponUiState.value as? CouponUiState.Success)?.selectedCoupon ?: newCoupon)
                        )
                        onRedeemSuccess()
                    }
                }
            }
        }
    }

    fun handleCouponSelection(coupon: CustomerCoupon) {
        if ((_couponUiState.value as? CouponUiState.Success)?.selectedCoupon == coupon) {
            _couponUiState.value = CouponUiState.Success(
                ((_couponUiState.value as? CouponUiState.Success)?.data ?: emptyList())
            )
        } else {
            _couponUiState.value = CouponUiState.Success(
                ((_couponUiState.value as? CouponUiState.Success)?.data ?: emptyList()),
                coupon
            )
        }
    }

    fun checkCoupon(
        coupon: CustomerCoupon,
        totalAmount: Double
    ): CustomerCoupon.Companion.CouponUsageResult {
        return coupon.checkAndCalculateForDiscount(totalAmount)
    }

    private suspend fun addCouponToServer() {
        val coupons = mutableListOf<ServerCoupon>(
            ServerCoupon(
                code = "NEWLADOSER",
                discountPercentage = 10,
                startDate = LocalDateTime.of(2025, 1, 1, 0, 0).toTimestamp(
                    zoneId = currentHostTimeZoneInString()
                ),
                endDate = LocalDateTime.of(2025, 2, 1, 0, 0).toTimestamp(
                    zoneId = currentHostTimeZoneInString()
                ),
                autoFetching = true
            ),
            ServerCoupon(
                code = "OLALADOS",
                discountPercentage = 15,
                startDate = LocalDateTime.of(2025, 1, 1, 0, 0).toTimestamp(
                    zoneId = currentHostTimeZoneInString()
                ),
                endDate = LocalDateTime.of(2025, 1, 16, 0, 0).toTimestamp(
                    zoneId = currentHostTimeZoneInString()
                ),
                autoFetching = true
            ),
            ServerCoupon(
                code = "LADOSISTHEBEST",
                discountPercentage = 15,
                startDate = LocalDateTime.of(2025, 1, 20, 0, 0).toTimestamp(
                    zoneId = currentHostTimeZoneInString()
                ),
                endDate = LocalDateTime.of(2025, 2, 1, 0, 0).toTimestamp(
                    zoneId = currentHostTimeZoneInString()
                ),
                autoFetching = true
            ),
            ServerCoupon(
                code = "NEWLADOSER2",
                discountPercentage = 20,
                startDate = LocalDateTime.of(2025, 1, 1, 0, 0).toTimestamp(
                    zoneId = currentHostTimeZoneInString()
                ),
                endDate = LocalDateTime.of(2025, 2, 1, 0, 0).toTimestamp(
                    zoneId = currentHostTimeZoneInString()
                ),
                maximumRedemption = 0
            ),
            ServerCoupon(
                code = "NEWLADOSER3",
                discountPercentage = 30,
                usageDuration = 60 * 60 * 24 * 7,
                maximumRedemption = 1
            )

        )
        coupons.forEach { coupon ->
            couponRepository.addCouponToServer(coupon)
        }
    }
}