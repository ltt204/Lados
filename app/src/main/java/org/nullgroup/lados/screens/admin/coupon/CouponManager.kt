package org.nullgroup.lados.screens.admin.coupon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.ModalBottomSheet
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.cart.ConfirmDialog
import org.nullgroup.lados.compose.cart.DialogInfo
import org.nullgroup.lados.compose.coupon.ServerCouponItem
import org.nullgroup.lados.data.models.ServerCoupon
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.datetime.currentHostTimeZoneInString
import org.nullgroup.lados.utilities.datetime.toDurationInSeconds
import org.nullgroup.lados.utilities.datetime.toLocalDateTime
import org.nullgroup.lados.viewmodels.admin.coupon.CouponManagerUiState
import org.nullgroup.lados.viewmodels.admin.coupon.CouponManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponManager(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    hideReturnButton: Boolean = true
) {
    val couponManagerViewModel = hiltViewModel<CouponManagerViewModel>()

    val couponUiState = couponManagerViewModel.couponUiState.collectAsStateWithLifecycle()
    val currentCoupons = remember(couponUiState.value) {
        (couponUiState.value as? CouponManagerUiState.Success)?.data ?: emptyList()
    }
    val selectedCouponId = remember(couponUiState.value) {
        (couponUiState.value as? CouponManagerUiState.Success)?.selectedCouponId
    }
    val selectedCoupon = remember(currentCoupons, selectedCouponId) {
        currentCoupons.find { it.id == selectedCouponId }
    }
    val isProcessing = remember(couponUiState.value) {
        (couponUiState.value as? CouponManagerUiState.Success)?.isProcessing == true
    }

    val currentDialogState = remember { mutableStateOf<DialogInfo?>(null) }
    val formOpenState = remember { mutableStateOf(false) }
    val currentTimeZone = currentHostTimeZoneInString()
    val scope = couponManagerViewModel.viewModelScope

    val onNavigateBack = {
        navController?.popBackStack()
    }

    val onCouponClicked = { coupon: ServerCoupon ->
        couponManagerViewModel.handleCouponSelection(coupon)
    }

    val onAddNewCoupon = {
        formOpenState.value = true
    }

    val onEditSelectedCoupon = { coupon: ServerCoupon ->
        formOpenState.value = true
    }

    val createCouponSucceededTitle = stringResource(R.string.coupon_create_success_title)
    val createCouponSucceededMessage = stringResource(R.string.coupon_create_success_message)
    val createCouponFailedTitle = stringResource(R.string.coupon_create_failed_title)
    val createCouponFailedMessage = stringResource(R.string.coupon_create_failed_message)
    val updateCouponSucceededTitle = stringResource(R.string.coupon_update_success_title)
    val updateCouponSucceededMessage = stringResource(R.string.coupon_update_success_message)
    val updateCouponFailedTitle = stringResource(R.string.coupon_update_failed_title)
    val updateCouponFailedMessage = stringResource(R.string.coupon_update_failed_message)

    val onFormSubmitted = fun(coupon: ServerCoupon) {
        if (selectedCoupon == null || coupon.id.isEmpty()) {
            scope.launch(Dispatchers.IO) {
                couponManagerViewModel.handleCouponCreation(
                    coupon = coupon,
                    onCreateSuccess = {
                        currentDialogState.value = DialogInfo(
                            titleText = createCouponSucceededTitle,
                            messageText = createCouponSucceededMessage,
                            onConfirm = { currentDialogState.value = null }
                        )
                        formOpenState.value = false
                    },
                    onCreateFailure = { error ->
                        currentDialogState.value = DialogInfo(
                            titleText = createCouponFailedTitle,
                            messageText = createCouponFailedMessage + " (${error?.message ?: "Unknown error"})",
                            onConfirm = { currentDialogState.value = null }
                        )
                    }
                )
            }
            return
        } else {
            scope.launch(Dispatchers.IO) {
                couponManagerViewModel.handleCouponUpdate(
                    updatedCoupon = coupon,
                    onUpdateSuccess = {
                        currentDialogState.value = DialogInfo(
                            titleText = updateCouponSucceededTitle,
                            messageText = updateCouponSucceededMessage,
                            onConfirm = { currentDialogState.value = null }
                        )
                        formOpenState.value = false
                    },
                    onUpdateFailure = { error ->
                        currentDialogState.value = DialogInfo(
                            titleText = updateCouponFailedTitle,
                            messageText = updateCouponFailedMessage + " (${error?.message ?: "Unknown error"})",
                            onConfirm = { currentDialogState.value = null }
                        )
                    }
                )
            }
        }
    }

    val deleteCouponSucceededTitle = stringResource(R.string.coupon_delete_success_title)
    val deleteCouponSucceededMessage = stringResource(R.string.coupon_delete_success_message)
    val deleteCouponFailedTitle = stringResource(R.string.coupon_delete_failed_title)
    val deleteCouponFailedMessage = stringResource(R.string.coupon_delete_failed_message)

    val onRemoveSelectedCoupon = {
        scope.launch(Dispatchers.IO) {
            couponManagerViewModel.handleCouponDeletion(
                onDeleteSuccess = {
                    currentDialogState.value = DialogInfo(
                        titleText = deleteCouponSucceededTitle,
                        messageText = deleteCouponSucceededMessage,
                        onConfirm = { currentDialogState.value = null }
                    )
                },
                onDeleteFailure = { error ->
                    currentDialogState.value = DialogInfo(
                        titleText = deleteCouponFailedTitle,
                        messageText = deleteCouponFailedMessage + " (${error?.message ?: "Unknown error"})",
                        onConfirm = { currentDialogState.value = null }
                    )
                }
            )
        }
    }

    val couponInfoOf = { coupon: ServerCoupon ->
        couponManagerViewModel.checkCoupon(coupon)
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
        modifier = modifier.padding(top = innerPadding.calculateTopPadding()),
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
                    if (navController != null && !hideReturnButton) {
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
                actions = {
                    IconButton(
                        onClick = {
                            if (selectedCoupon != null) {
                                onEditSelectedCoupon(selectedCoupon)
                            }
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit this coupon",
                                tint = iconTintColor,
                            )
                        },
                        enabled = selectedCoupon != null && !isProcessing,
                        colors = iconButtonColors,
                    )


                    IconButton(
                        onClick = {
                            onRemoveSelectedCoupon()
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove this coupon",
                                tint = iconTintColor,
                            )
                        },
                        enabled = selectedCoupon != null && !isProcessing,
                        colors = iconButtonColors
                    )
                },
                colors = topAppBarColor,
            )
        },
        floatingActionButton = {
            if (selectedCoupon == null && !isProcessing) {
                FloatingActionButton(
                    onClick = {
                        onAddNewCoupon()
                    },
                    content = @Composable {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add a new coupon",
                            tint = iconTintColor,
                        )
                    },
                    containerColor = LadosTheme.colorScheme.secondaryContainer,
                    contentColor = LadosTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    ) { innerScaffoldPadding ->
        ConfirmDialog(currentDialogState.value)

//        CouponFormEditor(
//            isOpened = formOpenState.value,
//            onSubmitted = {
//                onFormSubmitted(it)
//            },
//            onDismissed = { formOpenState.value = false },
//            modifier = Modifier
//                .width(400.dp)
//                .wrapContentHeight()
//                .padding(16.dp),
//            initialCoupon = selectedCoupon,
//        )

        CouponFormBottomSheet(
            isShownBottomSheet = formOpenState.value,
            changeBottomSheetState = { formOpenState.value = it },
            onFormSubmitted = onFormSubmitted,
            selectedCoupon = selectedCoupon,
        )

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
                                enabled = !isProcessing,
                                onItemClicked = { onCouponClicked(coupon) },
                                extraNote = couponInfo.extraNote,
                                trailingArea = couponInfo.trailingArea,
                                itemState = couponInfo.couponState,
                            )

                        }
                    }
                }

                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LadosTheme.colorScheme.surfaceContainerLowest),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = LadosTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .background(LadosTheme.colorScheme.surfaceContainerLowest)
                        )
                    }
                }
            }

            is CouponManagerUiState.Error -> {
                // Error
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponFormBottomSheet(
    isShownBottomSheet: Boolean,
    changeBottomSheetState: (Boolean) -> Unit,
    onFormSubmitted: (ServerCoupon) -> Unit,
    selectedCoupon: ServerCoupon? = null,
) {
    if (isShownBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { changeBottomSheetState(false) },
            modifier = Modifier,
            containerColor = LadosTheme.colorScheme.surfaceContainerLowest,
            contentColor = LadosTheme.colorScheme.onSurface,
            scrimColor = LadosTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5f),
        ) {
            CouponFormEditor(
                onSubmitted = onFormSubmitted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                initialCoupon = selectedCoupon,
            )
        }
    }
}