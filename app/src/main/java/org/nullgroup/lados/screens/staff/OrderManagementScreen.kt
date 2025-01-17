package org.nullgroup.lados.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.Screen.Customer.Order.OrderDetail.ID_ARG
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.toCurrency
import org.nullgroup.lados.utilities.toDateTimeString
import org.nullgroup.lados.viewmodels.staff.events.OrderScreenEvent
import org.nullgroup.lados.viewmodels.staff.order.OrderViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OrderListScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    orderViewModel: OrderViewModel = hiltViewModel(),
) {
    val orders by orderViewModel.orders.collectAsState()
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    val orderStatuses = remember {
        listOf(
            OrderStatus.CREATED,
            OrderStatus.CONFIRMED,
            OrderStatus.CANCELLED,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED,
            OrderStatus.RETURNED,
        )
    }

    LaunchedEffect(pagerState.currentPage) {
        orderViewModel.handleEvent(
            OrderScreenEvent.LoadOrders(
                orderStatuses[pagerState.currentPage],
                true
            )
        )
    }

    Column(
        modifier = modifier
            .padding(top = paddingValues.calculateTopPadding())
            .fillMaxSize()
    ) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .height(4.dp)
                        .background(LadosTheme.colorScheme.primary)
                )
            },
            edgePadding = LadosTheme.size.medium,
            containerColor = LadosTheme.colorScheme.surfaceContainer,
            contentColor = LadosTheme.colorScheme.onSurface,
        ) {
            orderStatuses.forEachIndexed { index, status ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = status.getIcon(),
                                contentDescription = status.name,
                                tint = if (pagerState.currentPage == index) LadosTheme.colorScheme.primary else LadosTheme.colorScheme.onBackground,
                                modifier = Modifier.size(LadosTheme.size.medium)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = status.name,
                                color = if (pagerState.currentPage == index) LadosTheme.colorScheme.primary else LadosTheme.colorScheme.onBackground,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                )
            }
        }

        HorizontalPager(
            count = orderStatuses.size,
            state = pagerState,
        ) { page ->
            OrderList(
                orders = orders.orders,
                isLoading = orders.isLoading,
                hasMoreOrders = orders.hasMoreOrders,
                onOrderClick = { orderId ->
                    navController.navigate("${Screen.Staff.OrderDetail.route}/$orderId")
                },
                onLoadMore = {
                    orderViewModel.handleEvent(
                        OrderScreenEvent.LoadOrders(
                            orderStatuses[page],
                            false
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun OrderList(
    orders: List<Order>,
    isLoading: Boolean,
    hasMoreOrders: Boolean,
    onOrderClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(orders) { order ->
            OrderListItem(
                order = order,
                onClick = { onOrderClick(order.orderId) }
            )
        }

        if (isLoading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(LadosTheme.size.medium)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

        if (hasMoreOrders) {
            item {
                LaunchedEffect(true) {
                    onLoadMore()
                }
            }
        }
    }
}

@Composable
fun OrderListItem(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LadosTheme.size.medium, vertical = LadosTheme.size.small)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(LadosTheme.size.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Order #${order.orderId.take(8)}",
                    style = LadosTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    )
                )
                Text(
                    text = order.lastUpdatedAt.toDateTimeString("dd/MM/yy HH:mm"),
                    style = LadosTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(LadosTheme.size.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${order.orderProducts.size} products",
                    color = LadosTheme.colorScheme.secondary
                )
                Text(
                    text = order.orderTotal.toCurrency(),
                    fontWeight = FontWeight.Bold,
                    color = LadosTheme.colorScheme.primary,
                )
            }
        }
    }
}