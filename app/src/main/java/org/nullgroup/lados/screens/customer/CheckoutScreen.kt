package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.nullgroup.lados.compose.cartRelated.CartItemBar
import org.nullgroup.lados.compose.cartRelated.PricingDetails
import org.nullgroup.lados.utilities.ToUSDCurrency
import org.nullgroup.lados.viewmodels.customer.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    innerPadding: PaddingValues = PaddingValues(bottom = 0.dp),
    modifier: Modifier = Modifier
) {
    val checkoutViewModel: CheckoutViewModel = hiltViewModel()

    val snackBarHostState = remember { mutableStateOf(SnackbarHostState()) }

    val orderingItems = checkoutViewModel.orderingItems.collectAsStateWithLifecycle()
    val orderingItemInformation = checkoutViewModel.orderingItemInformation.collectAsStateWithLifecycle()
    val checkoutDetail = checkoutViewModel.checkoutInfo.collectAsStateWithLifecycle()
    val scope = checkoutViewModel.viewModelScope

//    val onRefreshCompleted: (String) -> Unit = { message ->
//        scope.launch {
//            snackBarHostState.value.showSnackBar(
//                message = message,
//                duration = SnackBarDuration.Short
//            )
//        }
//    }
    // checkoutViewModel.onRefreshComplete = onRefreshCompleted

    // TODO: Remove the snackBar when the user navigates back
    val onNavigateBack = {
        scope.launch {
//            var result = scope.async {
//                checkoutViewModel.commitChangesToDatabase()
//            }.await()
//            if (result.isFailure) {
//                snackBarHostState.value.showSnackbar(
//                    message = result.exceptionOrNull()!!.message.toString(),
//                    duration = SnackbarDuration.Short
//                )
//            } else {
//                snackBarHostState.value.showSnackbar(
//                    message = "Successfully saved change(s) to the database",
//                    duration = SnackbarDuration.Short
//                )
//            }
        }

        navController.popBackStack()
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
                message = "Order created successful",
                duration = SnackbarDuration.Short
            )

            navController.popBackStack()
        }
    }
    val onCheckout = checkoutViewModel.checkoutHandler(
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
                    Text("Checkout", textAlign = TextAlign.Center)
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
            if (checkoutDetail.value == null) {
                return@Scaffold
            }
            val (subtotal, productDiscount, orderDiscount, total) = checkoutDetail.value!!
            CheckoutBottomBar(
                subtotal = subtotal.ToUSDCurrency(),
                productDiscount = productDiscount.ToUSDCurrency(),
                orderDiscount = orderDiscount.ToUSDCurrency(),
                total = total.ToUSDCurrency(),
                onCheckout = onCheckout,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        },
        content = { innerScaffoldPadding ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(innerScaffoldPadding)
                    .padding(horizontal = 20.dp)
                    .fillMaxSize()
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
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = { onCheckout() },
            modifier = Modifier
                .fillMaxWidth()
            ,
            colors = ButtonColors (
                containerColor = Color(0xFF9C27B0), // Lavender color
                contentColor = Color.White,
                disabledContentColor = Color.Gray,
                disabledContainerColor = Color.Black,
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
        Spacer(modifier = Modifier.height(8.dp))
    }
}