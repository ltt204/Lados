package org.nullgroup.lados.screens.customer.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.common.ProfileTopAppBar
import org.nullgroup.lados.compose.order.OrderScreenTopAppBar
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.viewmodels.customer.OrderState
import org.nullgroup.lados.viewmodels.customer.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    navController: NavController? = null,
    orderViewModel: OrderViewModel = hiltViewModel(),
) {
    val orderUiState = orderViewModel.orderState.collectAsState().value
    var tabSelectedIndex by rememberSaveable {
        mutableStateOf(0)
    }
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            ),
        containerColor = LadosTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = LadosTheme.colorScheme.background
                    )
            ) {
                ProfileTopAppBar(
                    onBackClick = { navController?.navigateUp() },
                    content = "Orders",
                )
                OrderScreenTopAppBar(
                    modifier = modifier
                        .padding(
                            start = LadosTheme.size.medium,
                            end = LadosTheme.size.medium,
                            top = LadosTheme.size.small,
                        ),
                    selectedTabIndex = tabSelectedIndex,
                    onTabSelected = {
                        tabSelectedIndex = it
                        orderViewModel.filterOrderByStatus(OrderStatus.entries[it])
                    })
            }
        }
    ) { innerPadding ->
        Column(modifier = modifier) {
            when (orderUiState) {
                is OrderState.Loading -> {
                    LoadOnProgress(
                        modifier = Modifier
                            .fillMaxWidth(),
                        content = {
                            CircularProgressIndicator(
                                color = LadosTheme.colorScheme.primary,
                            )
                        }
                    )
                }

                is OrderState.Error -> {

                }

                is OrderState.Success -> {
                    orderViewModel.filterOrderByStatus(OrderStatus.entries[tabSelectedIndex])
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(LadosTheme.size.medium),
                        verticalArrangement = Arrangement.spacedBy(LadosTheme.size.small),
                    ) {
                        items(items = orderUiState.orders, key = { it.orderId }) { order ->
                            OrderCard(
                                modifier = Modifier.fillMaxWidth(),
                                order = order,
                                onItemClick = {
                                    navController?.navigate("${Screen.Customer.Order.OrderDetail.route}/$it")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    order: Order,
    onItemClick: (String) -> Unit = {},
) {
    Card(
        modifier = modifier
            .height(84.dp),
        onClick = { onItemClick(order.orderId) },
        colors = CardColors(
            containerColor = LadosTheme.colorScheme.surfaceContainerHighest,
            contentColor = LadosTheme.colorScheme.onBackground,
            disabledContainerColor = LadosTheme.colorScheme.surfaceContainer,
            disabledContentColor = LadosTheme.colorScheme.onSurface,
        ),
    ) {
        Row(
            modifier = modifier
                .background(color = LadosTheme.colorScheme.background)
                .fillMaxSize()
                .padding(LadosTheme.size.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .padding(LadosTheme.size.small)
                    .width(LadosTheme.size.extraExtraLarge),
                painter = painterResource(id = R.drawable.baseline_receipt_long_24),
                contentDescription = "Order Icon",
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "${order.orderProducts.size} items",
                    style = LadosTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = LadosTheme.colorScheme.onBackground,
                    ),
                )
                Text(
                    text = "$${order.orderTotal}",
                    style = LadosTheme.typography.bodyMedium.copy(
                        color = LadosTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.3f),
                    ),
                )
            }
            IconButton(onClick = {
                onItemClick(order.orderId)
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}

// Write an preview about stepper in android
@Preview
@Composable
fun OrderScreenPreview() {
    OrderScreen()
}

@Preview
@Composable
fun OrderCardPreview() {
    OrderCard(
        modifier = Modifier.fillMaxWidth(),
        order = Order(
            orderId = "1",
            orderProducts = emptyList()
        )
    )
}