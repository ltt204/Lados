package org.nullgroup.lados.screens.customer.checkout

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.cart.CartItemBar
import org.nullgroup.lados.compose.cart.ConfirmDialog
import org.nullgroup.lados.compose.cart.DialogInfo
import org.nullgroup.lados.compose.cart.PricingDetails
import org.nullgroup.lados.compose.coupon.CouponSelector
import org.nullgroup.lados.data.models.Address
import org.nullgroup.lados.data.models.CustomerCoupon
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.coupon.CouponScreen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.toCurrency
import org.nullgroup.lados.viewmodels.customer.checkout.CheckoutError
import org.nullgroup.lados.viewmodels.customer.checkout.CheckoutViewModel
import org.nullgroup.lados.viewmodels.customer.checkout.InsufficientOrderProductInfo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    innerPadding: PaddingValues = PaddingValues(bottom = 0.dp),
    modifier: Modifier = Modifier
) {
    val checkoutViewModel: CheckoutViewModel = hiltViewModel()

    val snackBarHostState = remember { mutableStateOf(SnackbarHostState()) }
    val (isAllowedInteracting, setIsAllowedInteracting) = remember { mutableStateOf(true) }
    val (isCheckoutCompleted, setIsCheckoutCompleted) = remember { mutableStateOf(false) }

    val orderingItems = checkoutViewModel.orderingItems.collectAsStateWithLifecycle()
    val orderingItemInformation =
        checkoutViewModel.orderingItemInformation.collectAsStateWithLifecycle()
    val checkoutDetail = checkoutViewModel.checkoutInfo.collectAsStateWithLifecycle()
    val insufficientOrderItems =
        checkoutViewModel.insufficientOrderItems.collectAsStateWithLifecycle()
    val scope = checkoutViewModel.viewModelScope

    val userAddress = checkoutViewModel.userAddresses.collectAsStateWithLifecycle()
    val selectedAddress = checkoutViewModel.selectedAddress.collectAsStateWithLifecycle()
    val userPhoneNumber = checkoutViewModel.userPhoneNumber

    val appliedCoupon = checkoutViewModel.appliedCoupon.collectAsStateWithLifecycle()
    val setApplyingCoupon = { coupon: CustomerCoupon? ->
        checkoutViewModel.handleCouponChanged(coupon)
    }
    val (isShownBottomSheet, setIsShownBottomSheet) = remember { mutableStateOf(false) }

    checkoutViewModel.checkoutFailureHandler = { checkoutFailure ->
        when (checkoutFailure) {
            null -> {
                // Do nothing
            }

            CheckoutError.FAILED_TO_GET_USER_INFO -> {
                scope.launch {
                    snackBarHostState.value.showSnackbar(
                        message = "Failed to get user info",
                        duration = SnackbarDuration.Short
                    )
                }
            }

            CheckoutError.FAILED_TO_GET_CHECKOUT_INFO -> {
                scope.launch {
                    snackBarHostState.value.showSnackbar(
                        message = "Failed to get checkout info",
                        duration = SnackbarDuration.Short
                    )
                }
            }

            else -> {
                scope.launch {
                    snackBarHostState.value.showSnackbar(
                        message = "Unknown error occurred",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    val onClickEmptyAddressSelector: () -> Unit = {
        if (userAddress.value.isEmpty()) {
            scope.launch {
                snackBarHostState.value.showSnackbar(
                    message = "No address currently available!\nPlease provide one in the Profile section",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    val onNavigateBack = {
        setIsAllowedInteracting(false)
        navController.popBackStack()
    }

    val onCheckoutFailure: (String?) -> Unit = { errorString ->
        if (errorString != null) {
            scope.launch {
                snackBarHostState.value.showSnackbar(
                    message = errorString,
                    duration = SnackbarDuration.Short
                )
            }
        }
        setIsAllowedInteracting(true)
    }
    val onSuccessfulCheckout: () -> Unit = {
        setIsCheckoutCompleted(true)
//        scope.launch {
//            snackBarHostState.value.showSnackbar(
//                message = "Order created successful",
//                duration = SnackbarDuration.Short
//            )
//            navController.popBackStack()
//        }
    }
    val onCheckout = {
        setIsAllowedInteracting(false)
        checkoutViewModel.checkoutHandler(
            onCheckoutFailure,
            onSuccessfulCheckout
        ).invoke()
    }

    val defaultImageUrl = "https://placehold.co/600x400"
    val defaultTitle = stringResource(R.string.unknown_product)

    if (isCheckoutCompleted) {
        CheckoutCompleteScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateNextScreen = {
                navController.popBackStack()
                navController.navigate(Screen.Customer.Order.OrderList.route)
            },
            modifier = modifier.padding(innerPadding)
        )
        return
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
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.checkout),
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
                        enabled = isAllowedInteracting,
                        colors = iconButtonColors,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = iconTintColor,
                        )
                    }
                },
                colors = topAppBarColor,
            )
        },
        bottomBar = {
            if (checkoutDetail.value == null) {
                return@Scaffold
            }
            val (subtotal, productDiscount, orderDiscount) = checkoutDetail.value!!
            val total = checkoutDetail.value!!.total
            CheckoutBottomBar(
                subtotal = subtotal.toCurrency(),
                productDiscount = productDiscount.toCurrency(),
                orderDiscount = orderDiscount.toCurrency(),
                total = total.toCurrency(),
                onCheckout = onCheckout,
                checkoutEnabled = isAllowedInteracting && selectedAddress.value != null,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        },
        content = { innerScaffoldPadding ->
            Column(
                modifier = Modifier
                    .padding(innerScaffoldPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
//                    .scrollable(
//                        rememberScrollState(),
//                        orientation = Orientation.Vertical
//                        )
            ) {
                AddressSelector(
                    addresses = userAddress.value,
                    selectionEnabled = isAllowedInteracting,
                    selectedAddress = selectedAddress.value,
                    onClickExpander = onClickEmptyAddressSelector,
                    onAddressSelected = { address ->
                        checkoutViewModel.onAddressChanged(address)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                PhoneNumberCard(
                    phoneNumber = userPhoneNumber.value,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(orderingItems.value) { _, cartItem ->
                        if (cartItem.amount <= 0) {
                            return@itemsIndexed
                        }
                        val (product, productVariant) =
                            orderingItemInformation.value[cartItem.id] ?: Pair(null, null)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = LadosTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            CartItemBar(
                                imageUrl = productVariant?.images?.firstOrNull()?.link
                                    ?: defaultImageUrl,
                                title = product?.name ?: defaultTitle,
                                originalPrice = productVariant?.originalPrice!!.toCurrency(),
                                salePrice = productVariant.salePrice?.toCurrency(),
                                size = productVariant.size.sizeName,
                                color = productVariant.color.colorName,
                                quantity = cartItem.amount,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                    }
                } // End of LazyColumn
                Spacer(modifier = Modifier.height(8.dp))

                CouponSelector(
                    currentCoupon = appliedCoupon.value,
                    onClicked = {
                        setIsShownBottomSheet(true)
                    },
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (insufficientOrderItems.value.isNotEmpty()) {
                    InsufficientStockDialog(
                        insufficientOrderItems = insufficientOrderItems.value,
                        onConfirm = { checkoutViewModel.clearInsufficientOrderItems.invoke() }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState.value) }
    )

    CouponBottomSheet(
        isShownBottomSheet = isShownBottomSheet,
        changeBottomSheetState = setIsShownBottomSheet,
        onCouponSelected = setApplyingCoupon,
        orderTotal = checkoutDetail.value?.let {
            it.subtotal - it.productDiscount
        } ?: 0.0
    )

    if (!isAllowedInteracting) {
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(LadosTheme.colorScheme.background.copy(alpha = 0.5f))
                .fillMaxSize()
        ) {
            CircularProgressIndicator(
                color = LadosTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(64.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CouponBottomSheet(
    isShownBottomSheet: Boolean,
    changeBottomSheetState: (Boolean) -> Unit,
    onCouponSelected: (CustomerCoupon?) -> Unit,
    orderTotal: Double,
) {
    if (isShownBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { changeBottomSheetState(false) },
            modifier = Modifier,
            containerColor = LadosTheme.colorScheme.surfaceContainerLowest,
            contentColor = LadosTheme.colorScheme.onSurface,
            scrimColor = LadosTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5f),

            ) {
            CouponScreen(
                isEditable = true,
                orderTotal = orderTotal,
                onCouponSelected = onCouponSelected
            )
        }
    }
}


@Composable
private fun CheckoutBottomBar(
    subtotal: String, productDiscount: String, orderDiscount: String?, total: String,
    isEnabled: Boolean = true,
    onCheckout: () -> Unit = {},
    checkoutEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        if (isEnabled) {
            PricingDetails(
                subtotal = subtotal,
                productDiscount = productDiscount,
                orderDiscount = orderDiscount,
                total = total
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        val buttonColors = ButtonColors(
            containerColor = LadosTheme.colorScheme.primary,
            contentColor = LadosTheme.colorScheme.onPrimary,
            disabledContentColor = LadosTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
            disabledContainerColor = LadosTheme.colorScheme.primary.copy(alpha = 0.38f),
        )
        val textStyle = LadosTheme.typography.labelLarge.copy(
            color = LadosTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = { onCheckout() },
            enabled = isEnabled && checkoutEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            colors = buttonColors,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = total,
                    style = textStyle
                )

                Text(
                    text = stringResource(R.string.checkout_place_order),
                    style = textStyle
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressSelector(
    addresses: List<Address>,
    selectionEnabled: Boolean = true,
    selectedAddress: Address? = null,
    onClickExpander: (() -> Unit)? = null,
    onAddressSelected: ((Address) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { setExpanded(it) }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(8.dp)
                .background(
                    color = LadosTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(8.dp),
                )
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(end = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.shipping_address),
                    color = LadosTheme.colorScheme.onPrimaryContainer,
                    style = LadosTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedAddress?.toString() ?: stringResource(R.string.select_address),
                    color = LadosTheme.colorScheme.onPrimaryContainer,
                    style = LadosTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            IconButton(
                onClick = {
                    setExpanded(!expanded)
                    onClickExpander?.invoke()
                },
                enabled = selectionEnabled,
                modifier = Modifier
                    .wrapContentWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Expand",
                    tint = LadosTheme.colorScheme.onPrimaryContainer
                )
            }
        }

//        OutlinedButton(
//            onClick = { setExpanded(!expanded) },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            if (selectedAddress != null) {
//                Text(selectedAddress.toString())
//            } else {
//            Text("Select Address")
//                }
//        }

        ExposedDropdownMenu(
            expanded = expanded && selectionEnabled,
            onDismissRequest = { setExpanded(false) },
            containerColor = LadosTheme.colorScheme.secondaryContainer,
        ) {
            addresses.forEach { address ->
                DropdownMenuItem(
                    onClick = {
                        onAddressSelected?.invoke(address)
                        setExpanded(false)
                    },
                    text = {
                        Text(
                            address.toString(),
                            color = LadosTheme.colorScheme.onSecondaryContainer
                        )
                    }
                )
                HorizontalDivider(
                    color = LadosTheme.colorScheme.outline,
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
private fun PhoneNumberCard(
    phoneNumber: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .background(LadosTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.contact_phone_number),
                color = LadosTheme.colorScheme.onPrimaryContainer,
                style = LadosTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = phoneNumber,
                color = LadosTheme.colorScheme.onPrimaryContainer,
                style = LadosTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun ProductItemWithStockComparison(
    productInfo: InsufficientOrderProductInfo,
    modifier: Modifier = Modifier
) {
    val containerColor = LadosTheme.colorScheme.surfaceContainerHigh
    val bodyMediumTypo = LadosTheme.typography.bodyMedium
    val bodySmallTypo = LadosTheme.typography.bodySmall

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
    ) {
        Text(
            text = productInfo.productName,
            style = bodyMediumTypo.copy(fontWeight = FontWeight.Bold),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(4.dp)
        )

        Row(
            modifier = Modifier
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val specialStyle = bodySmallTypo.toSpanStyle().copy(
                fontWeight = FontWeight.Bold
            )
            val normalStyle = bodySmallTypo.toSpanStyle()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(normalStyle) {
                            append("Size - ")
                        }
                        withStyle(specialStyle) {
                            append(productInfo.productSize)
                        }
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(normalStyle) {
                            append(stringResource(id = R.string.product_color) + " - ")
                        }
                        withStyle(specialStyle) {
                            append(productInfo.productColor)
                        }
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(normalStyle) {
                            append(stringResource(R.string.ordered))
                        }
                        withStyle(specialStyle) {
                            append(productInfo.orderedAmount.toString())
                        }
                    },
                    style = bodyMediumTypo
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(normalStyle) {
                            append(stringResource(R.string.available))
                        }
                        withStyle(specialStyle) {
                            append(productInfo.availableStock.toString())
                        }
                    },
                    style = bodyMediumTypo
                )
            }

        }
    }
}


@Composable
private fun InsufficientStockDialog(
    insufficientOrderItems: List<InsufficientOrderProductInfo>,
    onConfirm: () -> Unit,
) {
    val dialogMessage = @Composable {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.out_of_stock),
                style = LadosTheme.typography.bodyMedium
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(insufficientOrderItems) { _, productInfo ->
                    ProductItemWithStockComparison(productInfo)
                }
            }

            Text(
                text = stringResource(R.string.out_of_stock_message),
                style = LadosTheme.typography.bodyMedium
            )
        }

    }

    ConfirmDialog(
        DialogInfo(
            titleText = stringResource(R.string.order_creation_failed),
            message = dialogMessage,
            onConfirm = onConfirm,
            confirmText = "Close",
        )
    )

//    AlertDialog(
//        onDismissRequest = onConfirm,
//        title = {
//            Text("Order creation failed")
//        },
//        text = {
//            Column (
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//                modifier = Modifier.padding(8.dp)
//            ) {
//                Text(
//                    text = "The following item(s) have insufficient stock:",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//
//                LazyColumn (
//                    verticalArrangement = Arrangement.spacedBy(4.dp),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    itemsIndexed(insufficientOrderItems) { _, productInfo ->
//                        ProductItemWithStockComparison(productInfo)
//                    }
//                }
//
//                Text (
//                    text = "Please adjust the quantity of the item(s) in your cart and try again later.",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//
//        },
//        confirmButton = {
//            OutlinedButton(
//                onClick = onConfirm,
//                modifier = Modifier.padding(8.dp)
//            ) {
//                Text("Close")
//            }
//        },
//        modifier = modifier
//    )
}

@Composable
private fun CheckoutCompleteScreen(
    onNavigateBack: (() -> Unit)? = null,
    backScreenText: String = stringResource(R.string.return_to_cart),
    onNavigateNextScreen: (() -> Unit)? = null,
    nextScreenText: String = stringResource(R.string.view_order),
    modifier: Modifier = Modifier
) {
    val topHalfColor = LadosTheme.colorScheme.primary
    val bottomHalfColor = LadosTheme.colorScheme.background

    val orderPlacedTitleStyle = LadosTheme.typography.bodyLarge.copy(
        color = LadosTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    )
    val orderPlacedNoteStyle = LadosTheme.typography.bodyLarge.copy(
        color = LadosTheme.colorScheme.onBackground
    )

    val buttonColors = ButtonColors(
        containerColor = LadosTheme.colorScheme.primary,
        contentColor = LadosTheme.colorScheme.onPrimary,
        disabledContentColor = LadosTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
        disabledContainerColor = LadosTheme.colorScheme.primary.copy(alpha = 0.38f),
    )

    Box(
        modifier = modifier.fillMaxSize(),
    ) {

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .background(topHalfColor)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.checkout_complete_image),
                    contentDescription = stringResource(R.string.order_complete),
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                        .size(120.dp)
                )
            }

//        val offset = animateOffsetAsState(
//            targetValue = Offset(0f, 0f),
//            animationSpec = spring(
//                dampingRatio = Spring.DampingRatioMediumBouncy,
//                stiffness = Spring.StiffnessLow
//            ),
//            label = "Box-go-up animation"
//        )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    //.offset(offset.value.x.dp, offset.value.y.dp)
                    .fillMaxWidth()
                    .weight(0.4f)
                    .offset(y = (-16).dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    )
                    .background(bottomHalfColor)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                    ) {
                        Text(
                            text = stringResource(R.string.order_placed_successfully),
                            style = orderPlacedTitleStyle,
                        )
                    }

                    Text(
                        text = stringResource(R.string.your_order_has_been_successfully_created),
                        textAlign = TextAlign.Start,
                        lineHeight = 40.sp,
                        style = orderPlacedNoteStyle,
                        modifier = Modifier.weight(0.3f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(0.2f)
                    ) {
                        Button(
                            onClick = { onNavigateBack?.invoke() },
                            colors = buttonColors,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                backScreenText, style = LadosTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onNavigateNextScreen?.invoke() },
                            colors = buttonColors,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                nextScreenText, style = LadosTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

