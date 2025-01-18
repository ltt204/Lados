package org.nullgroup.lados.screens.customer.cart

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.cart.CartItemBar
import org.nullgroup.lados.compose.cart.ConfirmDialog
import org.nullgroup.lados.compose.cart.DialogInfo
import org.nullgroup.lados.compose.cart.PricingDetails
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.toCurrency
import org.nullgroup.lados.viewmodels.customer.cart.CartViewModel

enum class ItemState {
    SELECTED,
    UNSELECTED,
    INVALID,
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CartScreen(
    navController: NavController,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val cartViewModel: CartViewModel = hiltViewModel()

    val snackBarHostState = remember { mutableStateOf(SnackbarHostState()) }
    val currentDialogState = remember { mutableStateOf<DialogInfo?>(null) }
    val (isAllowedInteracting, setIsAllowedInteracting) = remember { mutableStateOf(true) }

    val isLoadingItems = cartViewModel.isLoading.collectAsStateWithLifecycle().value
    val cartItems = cartViewModel.cartItems.collectAsStateWithLifecycle()
    val isAnyItemsExisted = remember {
        derivedStateOf { cartItems.value.any { it.amount > 0 } }
    }
    val cartItemInformation = cartViewModel.cartItemInformation.collectAsStateWithLifecycle()
    val selectedItems = cartViewModel.selectedCartItems.collectAsStateWithLifecycle().value
    val validSelectedItems = cartViewModel.validSelectedItems.collectAsStateWithLifecycle().value
    val isAnyValidItemSelected = remember(validSelectedItems.size) {
        derivedStateOf { validSelectedItems.isNotEmpty() }
    }
    val checkoutDetail = cartViewModel.checkoutDetail
    val scope = cartViewModel.viewModelScope

    val onRefreshError: (String) -> Unit = { message ->
        scope.launch {
            snackBarHostState.value.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
    cartViewModel.onRefreshError = onRefreshError

    val onItemSelected = { cartItem: CartItem ->
        if (isAllowedInteracting) {
            if (cartItem in selectedItems) {
                cartViewModel.onCartItemSelectionChanged(cartItem.id, false)
            } else {
                cartViewModel.onCartItemSelectionChanged(cartItem.id, true)
            }
        }
    }

    val onItemAmountChanged = { cartItem: CartItem, amountDelta: Int ->
        cartViewModel.updateCartItemAmountLocally(cartItem.id, amountDelta)
    }

    val isAllItemSelected = {
        cartItems.value.isNotEmpty() && selectedItems.size == cartItems.value.size
    }
    val onSelectedAllItems = { cartViewModel.selectAllCartItems() }
    val onUnselectedAllItems = { cartViewModel.unselectAllCartItems() }

    val onItemRemove = { cartItem: CartItem ->
        currentDialogState.value = DialogInfo(
            titleText = context.getString(R.string.remove_cart_item),
            messageText = context.getString(R.string.remove_cart_item_message),
            onConfirm = {
                onItemAmountChanged(cartItem, -cartItem.amount)
                currentDialogState.value = null
            },
            onCancel = {
                currentDialogState.value = null
            }
        )
    }

    val onSelectedItemsRemoved = {
        currentDialogState.value = DialogInfo(
            titleText = "Remove selected item${if (selectedItems.size > 1) "s" else ""}",
            messageText = "Are you sure you want to remove the selected item${if (selectedItems.size > 1) "s" else ""} from the cart?",
            onConfirm = {
                cartViewModel.removeSelectedCartItemLocally()
                currentDialogState.value = null
            },
            onCancel = {
                currentDialogState.value = null
            }
        )
        // cartViewModel.removeSelectedCartItemLocally()
    }

    val onNavigateBack = {
        setIsAllowedInteracting(false)
        scope.launch {
            scope.async {
                cartViewModel.commitChangesToDatabase()
            }.await()
            navController.popBackStack()
        }
    }

    val onCheckoutConfirmation = {
        val invalidCount = selectedItems.size - validSelectedItems.size

        // Don't question about the ugly } at the start of the line
        // It helps with line continuation so that the actual code line not becomes too long
        currentDialogState.value = DialogInfo(
            titleText = "Checkout Confirmation",
            messageText =
            """
                There ${
                if (invalidCount > 1) "are" else "is"
            } ${
                selectedItems.size - validSelectedItems.size
            } item${if (invalidCount > 1) "s" else ""} in the cart that are currently invalid!
                
                ${if (invalidCount > 1) "They" else "It"} will be ignored when checking out!
                
                Are you sure you want to checkout?
                """.trimIndent(),
            onConfirm = {
                cartViewModel.reconfirmOnInvalidItems()
            },
            onCancel = {
                setIsAllowedInteracting(true)
                currentDialogState.value = null
            }
        )
    }

    val onSuccessfulCheckout: () -> Unit = {
        navController.navigate(Screen.Customer.CheckOutScreen.route)
    }

    val onCheckoutFailure: (String) -> Unit = { errorString ->
        setIsAllowedInteracting(true)
        scope.launch {
            snackBarHostState.value.showSnackbar(
                message = "Something wrong happened while checking out: $errorString",
                duration = SnackbarDuration.Short
            )
        }
    }

    val onCheckingOut = {
        setIsAllowedInteracting(false)
        cartViewModel.checkingOutHandler(
            onCheckoutConfirmation,
            onSuccessfulCheckout,
            onCheckoutFailure,
        ).invoke()
    }


    val defaultImageUrl = "https://placehold.co/600x400"
    val defaultTitle = "Unknown Product"
    val defaultValue = "???"

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
        containerColor = LadosTheme.colorScheme.background,
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.cart_title),
                        textAlign = TextAlign.Center,
                        style = LadosTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = LadosTheme.colorScheme.onSurface,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigateBack() },
                        enabled = isAllowedInteracting,
                        colors = iconButtonColors
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = iconTintColor
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isAllItemSelected()) {
                                onUnselectedAllItems()
                            } else {
                                onSelectedAllItems()
                            }
                        },
                        content = {
                            if (isAllItemSelected()) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_deselect),
                                    contentDescription = "Unselected all items from cart",
                                    tint = iconTintColor,
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_select_all),
                                    contentDescription = "Selected all items from cart",
                                    tint = iconTintColor,
                                )
                            }
                        },
                        enabled = !isLoadingItems && isAnyItemsExisted.value && isAllowedInteracting,
                        colors = iconButtonColors,
                    )


                    IconButton(
                        onClick = {
                            onSelectedItemsRemoved()
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove selected items from cart",
                                tint = iconTintColor,
                            )
                        },
                        enabled = !isLoadingItems && selectedItems.isNotEmpty() && isAllowedInteracting,
                        colors = iconButtonColors
                    )
                },
                colors = topAppBarColor,
            )
        },
        bottomBar = {
            if (isAnyItemsExisted.value) {
                var total: Double
                val (subtotal, productDiscount, orderDiscount) = checkoutDetail().also {
                    total = it.total
                }
                CartBottomBar(
                    subtotal = subtotal.toCurrency(),
                    productDiscount = productDiscount.toCurrency(),
                    orderDiscount = orderDiscount.toCurrency(),
                    total = total.toCurrency(),
                    isEnabled = isAnyValidItemSelected.value,
                    onCheckout = onCheckingOut,
                    checkoutEnabled = isAllowedInteracting,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        },
        content = { innerScaffoldPadding ->
            ConfirmDialog(currentDialogState.value)

            if (isLoadingItems) {
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
                // } else if (cartItems.value.isEmpty()) { // Not working
            } else if (isAnyItemsExisted.value.not()) {
                Column(
                    modifier = Modifier
                        .padding(innerScaffoldPadding)
                        .padding(horizontal = 20.dp)
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty_cart_image),
                        contentDescription = "Empty cart",
                        modifier = Modifier.width(120.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.cart_empty_message),
                        textAlign = TextAlign.Center,
                        style = LadosTheme.typography.bodyLarge.copy(
                            color = LadosTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            lineHeight = 40.sp
                        )
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(innerScaffoldPadding)
                        .padding(horizontal = 20.dp)
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background)
                ) {
                    itemsIndexed(cartItems.value) { _, cartItem ->
                        if (cartItem.amount <= 0) {
                            return@itemsIndexed
                        }
                        val (product, productVariant) =
                            cartItemInformation.value[cartItem.id] ?: Pair(null, null)
                        val itemState = if (cartItem in validSelectedItems) {
                            ItemState.SELECTED
                        } else if (cartItem in selectedItems || productVariant == null) {
                            ItemState.INVALID
                        } else {
                            ItemState.UNSELECTED
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            CartItemBar(
                                imageUrl = productVariant?.images?.firstOrNull()?.link
                                    ?: defaultImageUrl,
                                title = product?.name ?: defaultTitle,
                                originalPrice = productVariant?.originalPrice.toCurrency(),
                                salePrice = productVariant?.salePrice?.toCurrency(),
                                size = productVariant?.size?.sizeName ?: defaultValue,
                                color = productVariant?.color?.colorName ?: defaultValue,
                                clickEnabled = isAllowedInteracting,
                                onAddClick = { onItemAmountChanged(cartItem, 1) },
                                quantity = cartItem.amount,
                                onRemoveClick = {
                                    if (cartItem.amount == 1) {
                                        onItemRemove(cartItem)
                                    } else {
                                        onItemAmountChanged(cartItem, -1)
                                    }
                                },
                                onItemSelected = { onItemSelected(cartItem) },
                                itemState = itemState,
                                modifier = Modifier
                            )
                        }

                    }
                }
            }

        },
        snackbarHost = { SnackbarHost(snackBarHostState.value) }
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

@Composable
fun CartBottomBar(
    subtotal: String, productDiscount: String, orderDiscount: String?, total: String,
    isEnabled: Boolean = true,
    onCheckout: () -> Unit = {},
    checkoutEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
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
            Spacer(modifier = Modifier.height(8.dp))
        }

        val buttonColors = ButtonColors(
            containerColor = LadosTheme.colorScheme.primary,
            contentColor = LadosTheme.colorScheme.onPrimary,
            disabledContentColor = LadosTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
            disabledContainerColor = LadosTheme.colorScheme.primary.copy(alpha = 0.38f),
        )
        Button(
            onClick = { onCheckout() },
            enabled = isEnabled && checkoutEnabled,
            colors = buttonColors,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Text(stringResource(R.string.cart_checkout))
        }
    }
}
