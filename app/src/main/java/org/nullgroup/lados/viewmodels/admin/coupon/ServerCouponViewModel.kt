package org.nullgroup.lados.viewmodels.admin.coupon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.ServerCoupon
import org.nullgroup.lados.data.models.currentHostTimeZoneInString
import org.nullgroup.lados.data.models.toTimestamp
import org.nullgroup.lados.data.repositories.interfaces.coupon.CouponRepository
import java.time.LocalDateTime
import javax.inject.Inject

sealed class ServerCouponUiState {
    object Loading : ServerCouponUiState()
    object Empty : ServerCouponUiState()
    data class Success(
        val data: List<ServerCoupon>,
        val editingCoupon: ServerCoupon? = null
    ) : ServerCouponUiState()
    data class Error(val message: String) : ServerCouponUiState()
}

@HiltViewModel
class ServerCouponViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val couponRepository: CouponRepository
) : ViewModel() {
    private val _couponUiState = MutableStateFlow<ServerCouponUiState>(ServerCouponUiState.Loading)
    val couponUiState = _couponUiState.asStateFlow()

    private val _serverCoupons = MutableStateFlow<List<ServerCoupon>>(emptyList())

    private val adminId = firebaseAuth.currentUser?.uid

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // addCouponToServer()
            fetchCoupons()
        }
    }

    private suspend fun fetchCoupons() {
        adminId?.let { id ->
            couponRepository.getCouponsForAdmin().collect { newCoupons ->
                if (newCoupons.isEmpty()) {
                    _couponUiState.value = ServerCouponUiState.Empty
                } else {
                    _couponUiState.value = ServerCouponUiState.Success(newCoupons)
                }
                _serverCoupons.value = newCoupons
            }
        }
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