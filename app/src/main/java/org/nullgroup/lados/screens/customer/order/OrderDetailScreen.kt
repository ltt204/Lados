package org.nullgroup.lados.screens.customer.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.ProfileTopAppBar
import org.nullgroup.lados.compose.common.TwoColsItem
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.capitalizeWords
import org.nullgroup.lados.utilities.getFirstFourOrderStatuses
import org.nullgroup.lados.utilities.toDateTimeString
import org.nullgroup.lados.viewmodels.customer.OrderDetailState
import org.nullgroup.lados.viewmodels.customer.OrderDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    viewModel: OrderDetailViewModel = hiltViewModel<OrderDetailViewModel>(),
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val uiState = viewModel.orderDetailState.collectAsState()
    Scaffold(
        modifier = modifier.padding(top = paddingValues.calculateTopPadding()),
        topBar = {
            ProfileTopAppBar(
                onBackClick = {
                    navController?.navigateUp()
                },
                content = "Order Detail"
            )
        }
    ) { innerPadding ->
        // Column for showing order status
        when (uiState.value) {
            is OrderDetailState.Error -> {
                Text(text = (uiState.value as OrderDetailState.Error).message)
            }

            is OrderDetailState.Loading -> {
                Text(text = "Loading")
            }

            is OrderDetailState.Success -> {
                val currentOrder = (uiState.value as OrderDetailState.Success).currentOrder
                Column(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = innerPadding.calculateTopPadding()
                    )
                ) {
                    OrderStatusArea(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        currentOrder = currentOrder
                    )
                    Spacer(modifier = Modifier.padding(top = 24.dp))
                    Column {
                        Text(
                            text = "Order Items",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.padding(bottom = 8.dp))
                        OrderItemsArea(
                            modifier = Modifier,
                            order = currentOrder,
                            onViewProductsClick = {
                                navController?.navigate(
                                    "${Screen.Customer.Order.OrderProductsView.route}/${currentOrder.orderId}"
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.padding(top = 24.dp))
                    Column {
                        Text(
                            text = "Delivery Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.padding(bottom = 8.dp))
                        DeliveryDetailArea(
                            modifier = Modifier.height(100.dp),
                            order = currentOrder
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusArea(
    modifier: Modifier = Modifier,
    currentOrder: Order,
    statuses: List<OrderStatus> = getFirstFourOrderStatuses()
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(statuses) { _, status ->
            val time: Long? = currentOrder.orderStatusLog[status.name]
            OrderStatusItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                status = Pair(status, time)
            )
        }
    }
}

@Composable
fun OrderStatusItem(
    modifier: Modifier = Modifier,
    status: Pair<OrderStatus, Long?> = Pair(OrderStatus.CREATED, null),
) {
    var tintColor = LocalContentColor.current
    val time: String = if (status.second == null) {
        tintColor = tintColor.copy(0.5f)
        "Not updated"
    } else {
        status.second!!.toDateTimeString("dd MMM")
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = tintColor
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Text(
                text = status.first.name.capitalizeWords(),
                style = MaterialTheme.typography.titleSmall,
                fontSize = 16.sp
            )
        }
        Text(text = time, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun OrderItemsArea(
    modifier: Modifier = Modifier,
    order: Order,
    onViewProductsClick: () -> Unit = {}
) {
    TwoColsItem(
        modifier = modifier,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(40.dp),
                    painter = painterResource(id = R.drawable.baseline_receipt_long_24),
                    contentDescription = "Order Icon"
                )
                Text(
                    text = "${order.orderProducts.size} items",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        trailingAction = {
            TextButton(onClick = { onViewProductsClick() }) {
                Text(
                    text = "View All",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        onClick = {
            onViewProductsClick()
        }
    )
}

@Composable
fun DeliveryDetailArea(
    modifier: Modifier = Modifier,
    order: Order
) {
    // TODO : Replace with actual delivery address and phone number
    val mockAddress = "123, ABC Street, XYZ City, 123456"
    val mockPhone = "+91 1234567890"
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { /*Do nothing*/ },
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = mockAddress,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = mockPhone,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderStatusItemProcessPreview() {
    OrderStatusItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        status = Pair(OrderStatus.CREATED, System.currentTimeMillis())
    )
}

@Preview(showBackground = true)
@Composable
fun OrderStatusItemConfirmedPreview() {
    OrderStatusItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        status = Pair(OrderStatus.CONFIRMED, null)
    )
}