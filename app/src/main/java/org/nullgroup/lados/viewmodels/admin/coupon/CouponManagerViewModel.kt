package org.nullgroup.lados.viewmodels.admin.coupon

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.compose.coupon.ItemState
import org.nullgroup.lados.data.models.ServerCoupon
import org.nullgroup.lados.utilities.datetime.currentHostTimeZoneInString
import org.nullgroup.lados.utilities.datetime.timestampFromNow
import org.nullgroup.lados.utilities.datetime.toTimestamp
import org.nullgroup.lados.data.repositories.interfaces.coupon.CouponRepository
import org.nullgroup.lados.screens.customer.coupon.CouponInfo
import java.time.LocalDateTime
import javax.inject.Inject

sealed class CouponManagerUiState {
    object Loading : CouponManagerUiState()
    object Empty : CouponManagerUiState()
    data class Success(
        val data: List<ServerCoupon>,
        val selectedCouponId: String? = null,
        val isProcessing: Boolean = false
    ) : CouponManagerUiState()

    data class Error(val message: String) : CouponManagerUiState()
}

@HiltViewModel
class CouponManagerViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val couponRepository: CouponRepository
) : ViewModel() {
    private val _couponUiState =
        MutableStateFlow<CouponManagerUiState>(CouponManagerUiState.Loading)
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
                    _couponUiState.value = CouponManagerUiState.Empty
                } else {
                    _couponUiState.value = CouponManagerUiState.Success(newCoupons)
                }
                _serverCoupons.value = newCoupons
            }
        }
    }

    fun handleCouponSelection(coupon: ServerCoupon) {
        _couponUiState.value = when (val currentState = _couponUiState.value) {
            is CouponManagerUiState.Success -> {
                val selectedCoupon = if (currentState.selectedCouponId == coupon.id) {
                    null
                } else {
                    coupon
                }
                Log.d("CouponManagerViewModel", "Selected coupon: ${selectedCoupon?.id ?: "null"}")
                currentState.copy(selectedCouponId = selectedCoupon?.id)
            }

            else -> currentState
        }

    }

    suspend fun handleCouponCreation(
        coupon: ServerCoupon,
        onCreateSuccess: (() -> Unit)? = null,
        onCreateFailure: ((Throwable?) -> Unit)? = null
    ) {
        _couponUiState.value = when (val currentState = _couponUiState.value) {
            is CouponManagerUiState.Success -> {
                currentState.copy(
                    isProcessing = true,
                )
            }
            else -> currentState
        }
        val insertionResult = viewModelScope.async(Dispatchers.IO) {
            couponRepository.addCouponToServer(coupon)
        }.await()

        if (insertionResult.isSuccess) {
            _couponUiState.value = when (val currentState = _couponUiState.value) {
                is CouponManagerUiState.Success -> {
                    currentState.copy(
                        selectedCouponId = insertionResult.getOrNull()!!,
                        isProcessing = false,
                    )
                }
                else -> currentState
            }

            onCreateSuccess?.invoke()
        } else {
            _couponUiState.value = when (val currentState = _couponUiState.value) {
                is CouponManagerUiState.Success -> {
                    currentState.copy(
                        isProcessing = false,
                    )
                }
                else -> currentState
            }
            onCreateFailure?.invoke(insertionResult.exceptionOrNull())
        }
    }

    suspend fun handleCouponUpdate(
        updatedCoupon: ServerCoupon,
        onUpdateSuccess: (() -> Unit)? = null,
        onUpdateFailure: ((Throwable?) -> Unit)? = null
    ) {
        _couponUiState.value = when (val currentState = _couponUiState.value) {
            is CouponManagerUiState.Success -> {
                currentState.copy(
                    isProcessing = true,
                )
            }
            else -> currentState
        }
        val updateResult = viewModelScope.async(Dispatchers.IO) {
            couponRepository.updateServerCoupon(updatedCoupon)
        }.await()

        _couponUiState.value = when (val currentState = _couponUiState.value) {
            is CouponManagerUiState.Success -> {
                currentState.copy(
                    isProcessing = false,
                )
            }
            else -> currentState
        }

        if (updateResult.isSuccess) {
            onUpdateSuccess?.invoke()
        } else {
            onUpdateFailure?.invoke(updateResult.exceptionOrNull())
        }
    }

    suspend fun handleCouponDeletion(
        onDeleteSuccess: (() -> Unit)? = null,
        onDeleteFailure: ((Throwable?) -> Unit)? = null
    ) {
        val selectedCouponId =
            (_couponUiState.value as? CouponManagerUiState.Success)?.selectedCouponId
        if (selectedCouponId != null) {
            _couponUiState.value = when (val currentState = _couponUiState.value) {
                is CouponManagerUiState.Success -> {
                    currentState.copy(
                        isProcessing = true,
                    )
                }

                else -> currentState
            }
            val deleteResult = viewModelScope.async(Dispatchers.IO) {
                couponRepository.deleteServerCoupon(selectedCouponId)
            }.await()

            if (deleteResult.getOrNull() == true) {
                _couponUiState.value = when (val currentState = _couponUiState.value) {
                    is CouponManagerUiState.Success -> {
                        currentState.copy(
                            selectedCouponId = null,
                            isProcessing = false,
                        )
                    }

                    else -> currentState
                }
                onDeleteSuccess?.invoke()
            } else {
                _couponUiState.value = when (val currentState = _couponUiState.value) {
                    is CouponManagerUiState.Success -> {
                        currentState.copy(
                            isProcessing = false,
                        )
                    }

                    else -> currentState
                }
                onDeleteFailure?.invoke(deleteResult.exceptionOrNull())
            }
        }
    }

    fun checkCoupon(coupon: ServerCoupon): CouponInfo {
        val selectedCouponId =
            (_couponUiState.value as? CouponManagerUiState.Success)?.selectedCouponId
        val isSelectedState =
            selectedCouponId?.let { if (it == coupon.id) ItemState.SELECTED else null }

        val currentTimestamp = timestampFromNow()
        if (coupon.startDate > currentTimestamp) {
            return CouponInfo(
                couponState = isSelectedState ?: ItemState.DISABLED,
                extraNote = "(Not started yet)"
            )
        } else {
            if (coupon.endDate <= currentTimestamp) {
                return CouponInfo(
                    couponState = isSelectedState ?: ItemState.DISABLED,
                    extraNote = "(Expired)"
                )
            }
        }
        if (coupon.maximumRedemption != null && coupon.redeemedCount >= coupon.maximumRedemption) {
            return CouponInfo(
                couponState = isSelectedState ?: ItemState.DISABLED,
                extraNote = "(Out of stock)"
            )
        }
        return CouponInfo(
            couponState = isSelectedState ?: ItemState.NORMAL
        )
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