package org.nullgroup.lados.screens.staff.ordermanagement

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.DefaultCenterTopAppBar
import org.nullgroup.lados.compose.common.TwoColsItem
import org.nullgroup.lados.compose.profile.ConfirmDialog
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.capitalizeWords
import org.nullgroup.lados.utilities.getActionsForButtonOfOrder
import org.nullgroup.lados.utilities.getByLocale
import org.nullgroup.lados.utilities.getColorByName
import org.nullgroup.lados.utilities.getOrderStatus
import org.nullgroup.lados.utilities.getStatusByName
import org.nullgroup.lados.utilities.toCurrency
import org.nullgroup.lados.utilities.toDateTimeString
import org.nullgroup.lados.utilities.updateOrderStatusByAction
import org.nullgroup.lados.viewmodels.staff.order.OrderDetailState
import org.nullgroup.lados.viewmodels.staff.order.OrderDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: OrderDetailViewModel = hiltViewModel<OrderDetailViewModel>(),
    context: Context = LocalContext.current,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val uiState by viewModel.uiState.collectAsState()
    var isConfirmedToCancelOrReturn by remember {
        mutableStateOf(false)
    }
    var actionForBottomButton by remember {
        mutableStateOf(listOf(Pair(null as String?) { _: NavController, _: String?, _: String? -> }))
    }
    var currentAction by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            DefaultCenterTopAppBar(
                onBackClick = {
                    navController.navigateUp()
                },
                content = "Order Detail"
            )
        },
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LadosTheme.size.small)
            ) {
                if (actionForBottomButton.isNotEmpty()) {
                    actionForBottomButton.forEach { action ->
                        action.first?.let {
                            if (it.isNotEmpty()) {
                                TextButton(modifier = Modifier
                                    .heightIn(min = 64.dp),
                                    onClick = {
                                        currentAction = it
                                        isConfirmedToCancelOrReturn = true
                                    }) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = it,
                                        color = getColorByName(it),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = LadosTheme.colorScheme.background,
    ) { innerPadding ->
        // Column for showing order status
        when (uiState) {
            is OrderDetailState.Error -> {
                (uiState as OrderDetailState.Error).message?.let { Text(text = it) }
            }

            is OrderDetailState.Loading -> {
                Text(text = "Loading")
            }

            is OrderDetailState.Success -> {
                val currentOrder = (uiState as OrderDetailState.Success).order
                Log.d("Staff:OrderDetailScreen", "Current Order: $currentOrder")
                actionForBottomButton =
                    getStatusByName(currentOrder.currentStatus).getActionsForButtonOfOrder(context)
                Column(
                    modifier = Modifier.padding(
                        start = LadosTheme.size.medium,
                        end = LadosTheme.size.medium,
                        top = innerPadding.calculateTopPadding()
                    )
                ) {
                    OrderStatusArea(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = LadosTheme.size.medium,
                                horizontal = LadosTheme.size.small
                            ),
                        currentOrder = currentOrder
                    )
                    Spacer(modifier = Modifier.padding(top = LadosTheme.size.small))
                    Column {
                        Text(
                            text = "Order Items",
                            style = LadosTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = LadosTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.padding(bottom = LadosTheme.size.small))
                        OrderItemsArea(
                            modifier = Modifier,
                            order = currentOrder,
                            onViewProductsClick = {
                                navController.navigate(
                                    "${Screen.Staff.OrderProducts.route}/${currentOrder.orderId}"
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.padding(LadosTheme.size.small))
                    Column {
                        Text(
                            text = "Delivery Details",
                            style = LadosTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = LadosTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.padding(bottom = LadosTheme.size.small))
                        DeliveryDetailArea(
                            modifier = Modifier
                                .wrapContentHeight()
                                .heightIn(min = 100.dp),
                            order = currentOrder
                        )
                    }
                }
            }
        }
    }

    if (isConfirmedToCancelOrReturn) {
        ConfirmDialog(
            title = {
                Text(text = "Are you sure?")
            },
            message = {
                Text(
                    text = "Are you sure you want to change the order status?"
                )
            },
            primaryButtonText = "Submit",
            secondaryButtonText = "Cancel",
            onDismissRequest = {
                isConfirmedToCancelOrReturn = false
            },
            confirmButton = {
                isConfirmedToCancelOrReturn = false
                if (uiState is OrderDetailState.Success) {
                    updateOrderStatusByAction(
                        viewModel,
                        (uiState as OrderDetailState.Success).order.orderId,
                        currentAction
                    )
                }
            }
        )
    }
}

@Composable
fun OrderStatusArea(
    modifier: Modifier = Modifier,
    currentOrder: Order,
    statuses: List<OrderStatus> = getOrderStatus(),
) {
    LazyColumn(modifier = modifier.height(250.dp)) {
        itemsIndexed(statuses) { _, status ->
            val time: Long? = currentOrder.orderStatusLog[status.name]
            OrderStatusItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LadosTheme.size.medium, horizontal = LadosTheme.size.small),
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
    val context = LocalContext.current
    var tintColor = LadosTheme.colorScheme.primary
    val time: String = if (status.second == null) {
        tintColor = tintColor.copy(0.5f)
        "Not updated"
    } else {
        status.second!!.toDateTimeString("dd/MM/yy HH:mm")
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
                color = LadosTheme.colorScheme.onBackground,
                style = LadosTheme.typography.titleMedium,
            )
        }
        Text(
            text = time,
            color = LadosTheme.colorScheme.onBackground,
            style = LadosTheme.typography.titleSmall
        )
    }
}

@Composable
fun OrderItemsArea(
    modifier: Modifier = Modifier,
    order: Order,
    onViewProductsClick: () -> Unit = {},
) {
    TwoColsItem(
        modifier = modifier,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier
                        .padding(LadosTheme.size.small)
                        .width(LadosTheme.size.extraExtraLarge),
                    painter = painterResource(id = R.drawable.frame_67),
                    contentDescription = "Order Icon"
                )
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "${order.orderProducts.size} items",
                        style = LadosTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                    )
                    Text(
                        text = order.orderTotal.toCurrency(),
                        style = LadosTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                    )
                }
            }
        },
        trailingAction = {
            TextButton(onClick = { onViewProductsClick() }) {
                Text(
                    text = "View all",
                    fontWeight = FontWeight.SemiBold,
                    color = LadosTheme.colorScheme.primary.copy(alpha = 0.8f)
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
    order: Order,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { /*Do nothing*/ },
        colors = CardColors(
            containerColor = LadosTheme.colorScheme.surfaceContainerHighest,
            contentColor = LadosTheme.colorScheme.onBackground,
            disabledContainerColor = LadosTheme.colorScheme.surfaceContainer,
            disabledContentColor = LadosTheme.colorScheme.onSurface,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(LadosTheme.size.large),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column {
                Text(
                    text = "Delivery Address: ",
                    style = LadosTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Text(
                    text = order.deliveryAddress,
                    style = LadosTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(LadosTheme.size.small))
            Row {
                Text(
                    text = "Customer Phone: ",
                    style = LadosTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Text(
                    text = order.customerPhoneNumber,
                    style = LadosTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderStatusItemProcessPreview() {
    OrderStatusItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = LadosTheme.size.medium, horizontal = LadosTheme.size.small),
        status = Pair(OrderStatus.CREATED, System.currentTimeMillis())
    )
}

@Preview(showBackground = true)
@Composable
fun OrderStatusItemConfirmedPreview() {
    OrderStatusItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = LadosTheme.size.medium, horizontal = LadosTheme.size.small),
        status = Pair(OrderStatus.CREATED, null)
    )
}

@Preview(showBackground = true)
@Composable
fun OrderItemsAreaPreview() {
    OrderItemsArea(
        modifier = Modifier,
        order = Order(
            orderId = "1",
            orderProducts = emptyList(),
            orderTotal = 0.0,
            deliveryAddress = "Kathmandu",
            customerPhoneNumber = "9841234567",
            orderStatusLog = emptyMap()
        ),
        onViewProductsClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun DeliveryDetailAreaPreview() {
    DeliveryDetailArea(
        modifier = Modifier.height(100.dp),
        order = Order(
            orderId = "1",
            orderProducts = emptyList(),
            orderTotal = 0.0,
            deliveryAddress = "Kathmandu",
            customerPhoneNumber = "9841234567",
            orderStatusLog = emptyMap()
        )
    )
}