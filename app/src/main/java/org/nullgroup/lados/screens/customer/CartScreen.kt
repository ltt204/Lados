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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.nullgroup.lados.compose.CartRelated.CartItemBar
import org.nullgroup.lados.compose.CartRelated.PricingDetails
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.viewmodels.CartViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CartScreen(
    innerPadding: PaddingValues = PaddingValues(bottom = 0.dp),
    modifier: Modifier = Modifier
) {
    val cartViewModel: CartViewModel = hiltViewModel()

    val cartItems = cartViewModel.cartItems.collectAsStateWithLifecycle()
    val cartItemInformation = cartViewModel.cartItemInformation.collectAsStateWithLifecycle()

    val selectedItems = remember { mutableStateListOf<CartItem>() }
    val checkoutDetail: (List<CartItem>) -> Triple<String, String, String> = { selectedItems ->
        val validItems = selectedItems.filter { cartItemInformation.value.containsKey(it.id) }
        val total = validItems.sumOf {
            (cartItemInformation.value[it.id]?.second?.salePrice ?: 0.0) * it.amount
        }
        val subtotal = validItems.sumOf {
            (cartItemInformation.value[it.id]?.second?.originalPrice ?: 0.0) * it.amount
        }
        val discount = subtotal - total
        Triple(subtotal.toString(), discount.toString(), total.toString())
    }

    val hardcodedCartId = "0"
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
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            val (subtotal, discount, total) = checkoutDetail(selectedItems)
            CartBottomBar(
                "$$subtotal", "$$discount", "$$total",
                isEnabled = selectedItems.isNotEmpty(),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        },
        floatingActionButton = {
            OutlinedButton(
                onClick = {
                    selectedItems.clear()
                    cartViewModel.getCartItems(hardcodedCartId)
                    cartViewModel.getItemsInformation()
                },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("Load Cart")
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
                itemsIndexed(cartItems.value) { index, cartItem ->
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
                                    if (!selectedItems.contains(cartItem)) {
                                        selectedItems.add(cartItem)
                                    } else {
                                        selectedItems.remove(cartItem)
                                    }
                                }
                            )
                            .background(
                                if (!selectedItems.contains(cartItem)) Color.Transparent
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
                            onAddClick = { /* TODO */ },
                            quantity = cartItem.amount,
                            onRemoveClick = { /* TODO */ },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                }
            }
        }

    )
}

@Composable
fun CartBottomBar(
    subtotal: String, discount: String, total: String,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isEnabled) {
            PricingDetails(
                subtotal = subtotal,
                discount = discount,
                total = total
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = { /* TODO */ },
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Checkout")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
