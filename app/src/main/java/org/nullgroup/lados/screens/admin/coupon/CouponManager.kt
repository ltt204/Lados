package org.nullgroup.lados.screens.admin.coupon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.cart.ConfirmDialog
import org.nullgroup.lados.compose.cart.DialogInfo
import org.nullgroup.lados.compose.coupon.ServerCouponItem
import org.nullgroup.lados.data.models.ServerCoupon
import org.nullgroup.lados.data.models.currentHostTimeZoneInString
import org.nullgroup.lados.data.models.toDurationInSeconds
import org.nullgroup.lados.data.models.toLocalDateTime
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.admin.coupon.CouponManagerUiState
import org.nullgroup.lados.viewmodels.admin.coupon.CouponManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponManager(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    innerPadding: PaddingValues = PaddingValues(0.dp),
) {
    val couponManagerViewModel = hiltViewModel<CouponManagerViewModel>()
    
    val couponUiState = couponManagerViewModel.couponUiState.collectAsStateWithLifecycle()
    val currentCoupons = (couponUiState.value as? CouponManagerUiState.Success)?.data ?: emptyList()
    val editingCoupon = (couponUiState.value as? CouponManagerUiState.Success)?.editingCoupon

    val currentDialogState = remember { mutableStateOf<DialogInfo?>(null) }
    val currentTimeZone = currentHostTimeZoneInString()
    
    val onNavigateBack = {
        navController?.popBackStack()
    }

    val couponInfoOf = { coupon: ServerCoupon ->
        couponManagerViewModel.checkCoupon(coupon)
    }

    val onCouponClicked = { coupon: ServerCoupon ->

    }
    
    val iconButtonColors = IconButtonColors(
        contentColor = LadosTheme.colorScheme.onSecondaryContainer,
        containerColor = LadosTheme.colorScheme.secondaryContainer,
        disabledContentColor = LadosTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.38f),
        disabledContainerColor = LadosTheme.colorScheme.secondaryContainer.copy(alpha = 0.38f),
    )
    val iconTintColor = LadosTheme.colorScheme.onSecondaryContainer
    val topAppBarColor = TopAppBarColors(
        containerColor = LadosTheme.colorScheme.surfaceContainerLowest,
        scrolledContainerColor = LadosTheme.colorScheme.surfaceContainerLow,
        navigationIconContentColor = LadosTheme.colorScheme.onSurface,
        actionIconContentColor = LadosTheme.colorScheme.onSurface,
        titleContentColor = LadosTheme.colorScheme.onSurface,
    )

    Scaffold(
        containerColor = LadosTheme.colorScheme.surfaceContainerLowest,
        modifier = modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.profile_coupons),
                        textAlign = TextAlign.Center,
                        style = LadosTheme.typography.titleLarge.copy(
                            color = LadosTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                },
                navigationIcon = {
                    if (navController != null) {
                        IconButton(
                            onClick = { onNavigateBack() },
//                        enabled = isAllowedInteracting,
                            colors = iconButtonColors,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                                contentDescription = stringResource(R.string.coupon_back_description),
                                tint = iconTintColor,
                            )
                        }
                    }
                },
                colors = topAppBarColor,
            )
        },
    ) { innerScaffoldPadding ->
        ConfirmDialog(currentDialogState.value)

        when (couponUiState.value) {
            is CouponManagerUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(innerScaffoldPadding)
                        .padding(horizontal = 20.dp)
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background)
                ) {
                    CircularProgressIndicator(
                        color = LadosTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(64.dp)
                    )
                }
            }

            is CouponManagerUiState.Empty -> {
                Column(
                    modifier = Modifier
                        .padding(innerScaffoldPadding)
                        .padding(horizontal = 20.dp)
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LadosTheme.colorScheme.background),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.love),
                            colorFilter = ColorFilter.tint(LadosTheme.colorScheme.onSurface),
                            contentDescription = stringResource(R.string.coupon_empty_description),
                            modifier = Modifier
                                .scale(2.0f)
                                .width(120.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.coupon_empty),
                            textAlign = TextAlign.Center,
                            style = LadosTheme.typography.bodyLarge.copy(
                                color = LadosTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                lineHeight = 40.sp
                            )
                        )
                    }
                }
            }

            is CouponManagerUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(innerScaffoldPadding)
                        .padding(horizontal = 20.dp)
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        itemsIndexed(items = currentCoupons) { _, coupon ->
                            val couponInfo = couponInfoOf(coupon)
                            ServerCouponItem(
                                couponCode = coupon.code,
                                discountPercentage = coupon.discountPercentage,
                                minimumOrderAmount = coupon.minimumOrderAmount,
                                maximumDiscount = coupon.maximumDiscount,
                                startDate = coupon.startDate.toLocalDateTime(currentTimeZone),
                                endDate = coupon.endDate.toLocalDateTime(currentTimeZone),
                                usageDuration = coupon.usageDuration?.toDurationInSeconds(),
                                redeemedCount = coupon.redeemedCount,
                                maximumRedemption = coupon.maximumRedemption,
                                autoFetching = coupon.autoFetching,
                                onItemClicked = { onCouponClicked(coupon) },
                                extraNote = couponInfo.extraNote,
                                trailingArea = couponInfo.trailingArea,
                                itemState = couponInfo.couponState,
                            )

                        }
                    }
                }
            }

            is CouponManagerUiState.Error -> {
                // Error
            }
        }
    }
}