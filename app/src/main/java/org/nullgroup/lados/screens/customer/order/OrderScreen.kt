package org.nullgroup.lados.screens.customer.order

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.common.ProfileTopAppBar
import org.nullgroup.lados.compose.order.OrderScreenTopAppBar
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.Typography
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.viewmodels.customer.OrderState
import org.nullgroup.lados.viewmodels.customer.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    navController: NavController? = null,
    orderViewModel: OrderViewModel = hiltViewModel()
) {
    val orderUiState = orderViewModel.orderState.collectAsState().value
    var tabSelectedIndex by rememberSaveable {
        mutableStateOf(0)
    }
    Scaffold(
        modifier = modifier.padding(top = paddingValues.calculateTopPadding()),
        topBar = {
            Column {
                ProfileTopAppBar(onBackClick = { navController?.navigateUp() }, content = "Orders")
                OrderScreenTopAppBar(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
                    selectedTabIndex = tabSelectedIndex,
                    onTabSelected = {
                        tabSelectedIndex = it
                        orderViewModel.filterOrderByStatus(OrderStatus.entries[it])
                    })
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
            when (orderUiState) {
                is OrderState.Loading -> {
                    LoadOnProgress(
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            CircularProgressIndicator()
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
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
    onItemClick: (String) -> Unit = {}
) {
    Card(
        modifier = modifier.height(84.dp),
        onClick = { onItemClick(order.orderId) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .width(40.dp),
                painter = painterResource(id = R.drawable.baseline_receipt_long_24),
                contentDescription = "Order Icon"
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "${order.orderProducts.size} items",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                Text(
                    text = "$${order.orderTotal}",
                    color = Color(0xFF272727).copy(alpha = 0.5f), fontSize = 16.sp
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