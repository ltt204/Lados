package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.nullgroup.lados.compose.cartRelated.CartItemBar
import org.nullgroup.lados.compose.cartRelated.PricingDetails
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.viewmodels.customer.CartViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CartScreen(
    innerPadding: PaddingValues = PaddingValues(bottom = 0.dp),
    modifier: Modifier = Modifier
) {
    val cartViewModel: CartViewModel = hiltViewModel()

    val snackBarHostState = remember { mutableStateOf(SnackbarHostState()) }

    val cartItems = cartViewModel.cartItems.collectAsStateWithLifecycle()
    val cartItemInformation = cartViewModel.cartItemInformation.collectAsStateWithLifecycle()
    val selectedItems = cartViewModel.selectedCartItems.collectAsStateWithLifecycle().value
    val isAnyItemSelected = cartViewModel.isAnyCartItemSelected()
    val checkoutDetail = cartViewModel.checkoutDetail
    val scope = cartViewModel.viewModelScope

    val onRefreshCompleted: (String) -> Unit = { message ->
        scope.launch {
            snackBarHostState.value.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
    cartViewModel.onRefreshComplete = onRefreshCompleted

    val onItemSelected = { cartItem: CartItem ->
        if (cartItem in selectedItems) {
            cartViewModel.onCartItemSelectionChanged(cartItem.id, false)
        } else {
            cartViewModel.onCartItemSelectionChanged(cartItem.id, true)
        }
    }

    val onItemAmountChanged = { cartItem: CartItem, amountDelta: Int ->
        cartViewModel.updateCartItemAmountLocally(cartItem.id, amountDelta)
    }

    val onSelectedItemsRemoved = {
        cartViewModel.removeSelectedCartItemLocally()
    }

    // TODO: Remove the snackBar when the user navigates back
    val onNavigateBack = {
        scope.launch {
            var result = scope.async {
                cartViewModel.commitChangesToDatabase()
            }.await()
            if (result.isFailure) {
                snackBarHostState.value.showSnackbar(
                    message = result.exceptionOrNull()!!.message.toString(),
                    duration = SnackbarDuration.Short
                )
            } else {
                snackBarHostState.value.showSnackbar(
                    message = "Successfully saved change(s) to the database",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    val onCheckoutFailure: (String) -> Unit = { errorString ->
        scope.launch {
            snackBarHostState.value.showSnackbar(
                message = errorString,
                duration = SnackbarDuration.Short
            )
        }
    }
    val onSuccessfulCheckout: () -> Unit = {
        scope.launch {
            snackBarHostState.value.showSnackbar(
                message = "Checkout successful",
                duration = SnackbarDuration.Short
            )
        }
    }
    val onCheckout = cartViewModel.checkoutHandler(
        onCheckoutFailure,
        onSuccessfulCheckout
    )

    val defaultImageUrl = "https://placehold.co/600x400"
    val defaultTitle = "Unknown Product"
    val defaultValue = "???"

    Scaffold (
        modifier = modifier.padding(innerPadding),
        topBar = {
            TopAppBar(
                title = {
                    Text("Cart", textAlign = TextAlign.Center)
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            val (subtotal, productDiscount, orderDiscount, total) = checkoutDetail()
            CartBottomBar(
                "$$subtotal", "$$productDiscount", "$$orderDiscount", "$$total",
                isEnabled = isAnyItemSelected,
                onCheckout = onCheckout,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        },
        floatingActionButton = {
            if (isAnyItemSelected) {
                OutlinedButton(
                    onClick = {
                        onSelectedItemsRemoved()
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Remove from cart")
                }
            }

        },
        content = { innerScaffoldPadding ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(innerScaffoldPadding)
                    .padding(horizontal = 20.dp)
                    .fillMaxSize()
            ) {
                itemsIndexed(cartItems.value) { _, cartItem ->
                    if (cartItem.amount <= 0) {
                        return@itemsIndexed
                    }
                    val (product, productVariant) =
                        cartItemInformation.value[cartItem.id] ?: Pair(null, null)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    // Prevent click if interacting with child components
                                    onItemSelected(cartItem)
                                }
                            )
                            .background(
                                if (cartItem !in selectedItems) Color.Transparent
                                else if (productVariant == null) Color.Red
                                else Color.LightGray,
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
                            onAddClick = { onItemAmountChanged(cartItem, 1) },
                            quantity = cartItem.amount,
                            onRemoveClick = {
                                onItemAmountChanged(cartItem, -1)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                }
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState.value) }
    )
}

@Composable
fun CartBottomBar(
    subtotal: String, productDiscount: String, orderDiscount: String?, total: String,
    isEnabled: Boolean = true,
    onCheckout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isEnabled) {
            PricingDetails(
                subtotal = subtotal,
                productDiscount = productDiscount,
                orderDiscount = orderDiscount,
                total = total
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = { onCheckout() },
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Checkout")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
