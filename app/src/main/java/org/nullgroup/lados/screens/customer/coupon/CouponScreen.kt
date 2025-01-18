package org.nullgroup.lados.screens.customer.coupon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import org.nullgroup.lados.compose.coupon.CouponItem
import org.nullgroup.lados.compose.coupon.ItemState
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.data.models.CustomerCoupon
import org.nullgroup.lados.data.models.currentHostTimeZoneInString
import org.nullgroup.lados.data.models.toLocalDateTime
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.customer.coupon.CouponUiState
import org.nullgroup.lados.viewmodels.customer.coupon.CouponViewModel

data class CouponInfo(
    val couponState: ItemState,
    val extraNote: String? = null,
    val trailingArea: (@Composable () -> Unit)? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponScreen(
    navController: NavController? = null,
    innerPadding: PaddingValues = PaddingValues(bottom = 0.dp),
    isEditable: Boolean = true,
    orderTotal: Double? = 5000.0,
    modifier: Modifier = Modifier
) {
    val couponViewModel: CouponViewModel = hiltViewModel()

    val couponUiState = couponViewModel.couponUiState.collectAsStateWithLifecycle()
    val currentCoupons = (couponUiState.value as? CouponUiState.Success)?.data ?: emptyList()
    val selectedCoupon = (couponUiState.value as? CouponUiState.Success)?.selectedCoupon

    val currentDialogState = remember { mutableStateOf<DialogInfo?>(null) }
    val currentTimeZone = currentHostTimeZoneInString()

    val onNavigateBack = {
        navController?.popBackStack()
    }

    val onCouponSelected = if (isEditable) { coupon: CustomerCoupon ->
        couponViewModel.handleCouponSelection(coupon)
    } else null

    val couponDiscountDesc = stringResource(R.string.coupon_usage_discount_desc)

    val couponNotEffectiveYet = stringResource(R.string.coupon_usage_not_effective_yet)
    val minimumOrderAmountNotReached = stringResource(R.string.coupon_usage_minimum_order_amount_not_reached)
    val couponExpired = stringResource(R.string.coupon_usage_expired)
    val couponAlreadyUsed = stringResource(R.string.coupon_usage_already_used)

    val failedUsageMessage = { error: CustomerCoupon.Companion.CouponUsageError ->
        when (error) {
            CustomerCoupon.Companion.CouponUsageError.COUPON_NOT_EFFECTIVE_YET -> couponNotEffectiveYet
            CustomerCoupon.Companion.CouponUsageError.MINIMUM_ORDER_AMOUNT_NOT_REACHED -> minimumOrderAmountNotReached
            CustomerCoupon.Companion.CouponUsageError.COUPON_EXPIRED -> couponExpired
            CustomerCoupon.Companion.CouponUsageError.COUPON_ALREADY_USED -> couponAlreadyUsed
        }
    }

    val couponInfoOf = { coupon: CustomerCoupon ->
        if (orderTotal == null) {
            CouponInfo(
                couponState = ItemState.NORMAL
            )
        } else {
            val usageResult = couponViewModel.checkCoupon(coupon, orderTotal)
            when (usageResult) {
                is CustomerCoupon.Companion.CouponUsageResult.Success -> {
                    val discountAmount = usageResult.discountAmount
                    CouponInfo(
                        couponState = if (selectedCoupon?.code == coupon.code) ItemState.SELECTED else ItemState.NORMAL,
                        extraNote = couponDiscountDesc.format(discountAmount),
//                        trailingArea = {
//                            Text(
//                                text = stringResource(
//                                    R.string.coupon_usage_discount_desc,
//                                    discountAmount
//                                ),
//                                style = LadosTheme.typography.bodyMedium.copy(
//                                    color = LadosTheme.colorScheme.onSecondaryContainer,
//                                    fontWeight = FontWeight.Bold
//                                )
//                            )
//                        }
                    )
                }
                is CustomerCoupon.Companion.CouponUsageResult.Error -> {
                    CouponInfo(
                        couponState = ItemState.DISABLED,
                        extraNote = failedUsageMessage(usageResult.error),
                    )
                }
            }
        }
    }

    val emptyCodeErrorTitle = stringResource(R.string.coupon_redeem_empty_title)
    val emptyCodeErrorMessage = stringResource(R.string.coupon_redeem_empty_message)
    val successRedemptionTitle = stringResource(R.string.coupon_redeem_success_title)
    val successRedemptionMessage = stringResource(R.string.coupon_redeem_success_message)
    val failedRedemptionTitle = stringResource(R.string.coupon_redeem_failed_title)

    val internalErrorRedemption = stringResource(R.string.coupon_redeem_failed_internal_error)
    val unavailableCouponRedemption = stringResource(R.string.coupon_redeem_failed_unavailable_coupon)
    val exceedMaximumRedemption = stringResource(R.string.coupon_redeem_failed_exceed_maximum_redemption)
    val expiredCouponRedemption = stringResource(R.string.coupon_redeem_failed_expired_coupon)
    val alreadyRedeemedCodeRedemption = stringResource(R.string.coupon_redeem_failed_already_redeemed_code)

    val failedRedemptionMessage = { error: CustomerCoupon.Companion.CouponRedemptionError ->
        when (error) {
            CustomerCoupon.Companion.CouponRedemptionError.INTERNAL_ERROR -> internalErrorRedemption
            CustomerCoupon.Companion.CouponRedemptionError.UNAVAILABLE_COUPON -> unavailableCouponRedemption
            CustomerCoupon.Companion.CouponRedemptionError.EXCEED_MAXIMUM_REDEMPTION -> exceedMaximumRedemption
            CustomerCoupon.Companion.CouponRedemptionError.EXPIRED_COUPON -> expiredCouponRedemption
            CustomerCoupon.Companion.CouponRedemptionError.ALREADY_REDEEMED_CODE -> alreadyRedeemedCodeRedemption
        }
    }

    val onRedeemClicked = fun(code: String) {
        val processedCode = code.trim().uppercase()
        if (processedCode.isEmpty()) {
            currentDialogState.value = DialogInfo(
                titleText = emptyCodeErrorTitle,
                messageText = emptyCodeErrorMessage,
                onConfirm = { currentDialogState.value = null }
            )
            return
        }

        couponViewModel.redeemCoupon(
            code = code,
            onRedeemSuccess = {
                currentDialogState.value = DialogInfo(
                    titleText = successRedemptionTitle,
                    messageText = successRedemptionMessage,
                    onConfirm = { currentDialogState.value = null }
                )
            },
            onRedeemFailed = {
                currentDialogState.value = DialogInfo(
                    titleText = failedRedemptionTitle,
                    messageText = failedRedemptionMessage(it),
                    onConfirm = { currentDialogState.value = null }
                )
            }

        )
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
                },
                colors = topAppBarColor,
            )
        },
    ) { innerScaffoldPadding ->
        ConfirmDialog(currentDialogState.value)

        when (couponUiState.value) {
            is CouponUiState.Loading -> {
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

            is CouponUiState.Empty -> {
                Column(
                    modifier = Modifier
                        .padding(innerScaffoldPadding)
                        .padding(horizontal = 20.dp)
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CouponRedeemArea(onRedeemClicked = onRedeemClicked)

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

            is CouponUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(innerScaffoldPadding)
                        .padding(horizontal = 20.dp)
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CouponRedeemArea(onRedeemClicked = onRedeemClicked)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        itemsIndexed(items = currentCoupons) { _, coupon ->
                            val couponInfo = couponInfoOf(coupon)
                            CouponItem(
                                couponCode = coupon.code,
                                discountPercentage = coupon.discountPercentage,
                                minimumOrderAmount = if (coupon.minimumOrderAmount <= 0.0) null else coupon.minimumOrderAmount,
                                maximumDiscount = coupon.maximumDiscount,
                                expiredAt = coupon.expiredAt.toLocalDateTime(currentTimeZone),
                                onItemClicked = { onCouponSelected?.invoke(coupon) },
                                extraNote = couponInfo.extraNote,
                                trailingArea = couponInfo.trailingArea,
                                itemState = couponInfo.couponState,
                            )
                        }
                    }
                }
            }

            is CouponUiState.Error -> {
                // Error
            }
        }
    }
}

@Composable
fun CouponRedeemArea(
    modifier: Modifier = Modifier,
    onRedeemClicked: (String) -> Unit,
) {
    val redeemingCode = remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomTextField(
            text = redeemingCode.value,
            onValueChange = { redeemingCode.value = it },
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.coupon_redeem_area_hint),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            modifier = Modifier.wrapContentHeight(),
            onClick = { onRedeemClicked(redeemingCode.value) },
            colors = ButtonColors(
                containerColor = LadosTheme.colorScheme.primaryContainer,
                contentColor = LadosTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = LadosTheme.colorScheme.primaryContainer.copy(alpha = 0.38f),
                disabledContentColor = LadosTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.38f),
            )
        ) {
            Text(
                text = stringResource(R.string.coupon_redeem_area_button)
            )
        }
    }

}