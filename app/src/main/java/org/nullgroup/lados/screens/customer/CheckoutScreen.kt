package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import org.nullgroup.lados.compose.cartRelated.CartItemBar
import org.nullgroup.lados.compose.cartRelated.PricingDetails
import org.nullgroup.lados.data.models.Address
import org.nullgroup.lados.utilities.toUSDCurrency
import org.nullgroup.lados.viewmodels.customer.CheckoutViewModel
import org.nullgroup.lados.viewmodels.customer.InsufficientOrderProductInfo

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
    val orderingItemInformation = checkoutViewModel.orderingItemInformation.collectAsStateWithLifecycle()
    val checkoutDetail = checkoutViewModel.checkoutInfo.collectAsStateWithLifecycle()
    val insufficientOrderItems = checkoutViewModel.insufficientOrderItems.collectAsStateWithLifecycle()
    val scope = checkoutViewModel.viewModelScope

    val userAddress = checkoutViewModel.userAddresses.collectAsStateWithLifecycle()
    val selectedAddress = checkoutViewModel.selectedAddress.collectAsStateWithLifecycle()
    val userPhoneNumber = checkoutViewModel.userPhoneNumber

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
    val defaultTitle = "Unknown Product"
    val defaultValue = "???"

    if (isCheckoutCompleted) {
        CheckoutCompleteScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            // TODO: Implement navigation to "See Order" screen
            onNavigateNextScreen = null,
            modifier = modifier.padding(innerPadding)
        )
        return
    }

    Scaffold (
        modifier = modifier.padding(innerPadding),
        topBar = {
            TopAppBar(
                title = {
                    Text("Checkout", textAlign = TextAlign.Center)
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }, enabled = isAllowedInteracting) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (checkoutDetail.value == null) {
                return@Scaffold
            }
            val (subtotal, productDiscount, orderDiscount, total) = checkoutDetail.value!!
            CheckoutBottomBar(
                subtotal = subtotal.toUSDCurrency(),
                productDiscount = productDiscount.toUSDCurrency(),
                orderDiscount = orderDiscount.toUSDCurrency(),
                total = total.toUSDCurrency(),
                onCheckout = onCheckout,
                checkoutEnabled = isAllowedInteracting && selectedAddress.value != null,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        },
        content = { innerScaffoldPadding ->
            Column(
                modifier = Modifier
                    .padding(innerScaffoldPadding)
                    .padding(horizontal = 20.dp)
                    .fillMaxSize()
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

                LazyColumn(
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
                                .combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        // Prevent click if interacting with child components
//                                    onItemSelected(cartItem)
                                    }
                                )
                                .background(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            CartItemBar(
                                imageUrl = productVariant?.images?.firstOrNull()?.link ?: defaultImageUrl,
                                title = product?.name ?: defaultTitle,
                                originalPrice = "$" + (productVariant?.originalPrice ?: defaultValue).toString(),
                                salePrice = "$" + (productVariant?.salePrice ?: defaultValue).toString(),
                                size = productVariant?.size?.sizeName ?: defaultValue,
                                color = productVariant?.color?.colorName ?: defaultValue,
                                quantity = cartItem.amount,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                    }
                } // End of LazyColumn
                Spacer(modifier = Modifier.height(4.dp))

                if (insufficientOrderItems.value.isNotEmpty()) {
                    InsufficientStockDialog(
                        insufficientOrderItems = insufficientOrderItems.value,
                        onDismiss = { checkoutViewModel.clearInsufficientOrderItems.invoke() }
                    )
                }
            }

        },
        snackbarHost = { SnackbarHost(snackBarHostState.value) }
    )
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

        OutlinedButton(
            onClick = { onCheckout() },
            enabled = isEnabled && checkoutEnabled,
            modifier = Modifier
                .fillMaxWidth()
            ,
            colors = ButtonColors (
                containerColor = Color(0xFF8E6CEF), // Lavender color
                contentColor = Color.White,
                disabledContentColor = Color.Gray,
                disabledContainerColor = Color.LightGray,
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = total,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "Place Order",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )
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
        Box(
            modifier = modifier
                .padding(8.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp))
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Shipping Address",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedAddress?.toString() ?: "Select Address",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(
                onClick = {
                    setExpanded(!expanded)
                    onClickExpander?.invoke() },
                enabled = selectionEnabled,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterEnd)
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Expand"
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
            onDismissRequest = { setExpanded(false) }
        ) {
            addresses.forEach { address ->
                DropdownMenuItem(
                    onClick = {
                        onAddressSelected?.invoke(address)
                        setExpanded(false)
                    },
                    text = {
                        Text(address.toString())
                    }
                )
                HorizontalDivider()
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
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Contact Phone Number",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = phoneNumber,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ProductItemWithStockComparison(
    productInfo: InsufficientOrderProductInfo,
    modifier: Modifier = Modifier
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = modifier
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        Text(
            text = productInfo.productName,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(4.dp)
        )

        Row(
            modifier = Modifier
                .padding(4.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val specialStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            val normalStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                color = Color.Gray
            )

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
                            append("Color - ")
                        }
                        withStyle(specialStyle) {
                            append(productInfo.productColor)
                        }
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column (
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(normalStyle) {
                            append("Ordered - ")
                        }
                        withStyle(specialStyle) {
                            append(productInfo.orderedAmount.toString())
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(normalStyle) {
                            append("Available - ")
                        }
                        withStyle(specialStyle) {
                            append(productInfo.availableStock.toString())
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }
}


@Composable
private fun InsufficientStockDialog(
    insufficientOrderItems: List<InsufficientOrderProductInfo>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Order creation failed")
        },
        text = {
            Column (
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "The following item(s) have insufficient stock:",
                    style = MaterialTheme.typography.bodyMedium
                )

                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(insufficientOrderItems) { _, productInfo ->
                        ProductItemWithStockComparison(productInfo)
                    }
                }

                Text (
                    text = "Please adjust the quantity of the item(s) in your cart and try again later.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        },
        confirmButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Close")
            }
        },
        modifier = modifier
    )
}

@Composable
private fun CheckoutCompleteScreen(
    onNavigateBack: (() -> Unit)? = null,
    backScreenText: String = "Return to Cart",
    onNavigateNextScreen: (() -> Unit)? = null,
    nextScreenText: String = "View Order",
    modifier: Modifier = Modifier
) {
    val purpleColor = Color(0xFF8E6CEF)

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
                    .background(purpleColor)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.checkout_complete_image),
                    contentDescription = "Order Complete",
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
                    .background(Color.White)
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
                            text = "Order Placed\nSuccessfully",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                            )
                        )
                    }

                    Text(
                        text = "Your order has been successfully created",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium,
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
                        val buttonColors = ButtonColors(
                            containerColor = purpleColor,
                            contentColor = Color.White,
                            disabledContentColor = Color.Gray,
                            disabledContainerColor = Color.LightGray
                        )
                        Button(
                            onClick = { onNavigateBack?.invoke() },
                            colors = buttonColors,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(backScreenText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onNavigateNextScreen?.invoke() },
                            colors = buttonColors,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(nextScreenText)
                        }
                    }
                }
            }
        }
    }
}

