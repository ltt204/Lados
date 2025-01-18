package org.nullgroup.lados.screens.admin.coupon

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import org.nullgroup.lados.data.models.ServerCoupon
import org.nullgroup.lados.data.models.currentHostTimeZoneInString
import org.nullgroup.lados.viewmodels.admin.coupon.CouponFormViewModel

// A very good guide: https://joebirch.co/android/building-an-authentication-form-using-jetpack-compose/

@Composable
fun SingleCouponEditor(
    modifier: Modifier = Modifier,
    initialCoupon: ServerCoupon? = null,
) {
    val couponFormViewModel: CouponFormViewModel = hiltViewModel()
    val zoneId = currentHostTimeZoneInString()

    val couponFormUiState = couponFormViewModel.couponFormUiState.collectAsState().value

    LaunchedEffect(initialCoupon) {
        initialCoupon?.let {
            couponFormViewModel.initialize(
                coupon = it,
                zoneId = zoneId
            )
        }
    }

    val isInteractable = couponFormUiState.isProcessing.not()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

    }
}