package org.nullgroup.lados.screens.staff.ordermanagement

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.screens.Screen
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
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

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
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                orderViewModel.handleEvent(OrderScreenEvent.SearchOrders(it))
            },
            isExpanded = isSearchExpanded,
            onExpandedChange = { isSearchExpanded = it },
            modifier = Modifier.padding(horizontal = LadosTheme.size.medium)
        )

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
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isExpanded) 96.dp else 64.dp)
            .padding(vertical = LadosTheme.size.small),
        shape = RoundedCornerShape(LadosTheme.size.medium),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandedChange(!isExpanded) }
                .padding(LadosTheme.size.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = LadosTheme.colorScheme.background,
            )
            Spacer(modifier = Modifier.width(LadosTheme.size.medium))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    color = LadosTheme.colorScheme.background,
                    fontSize = LadosTheme.typography.labelLarge.fontSize,
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = "Search by Order ID",
                                color = LadosTheme.colorScheme.background.copy(alpha = 0.5f),
                                fontSize = LadosTheme.typography.labelLarge.fontSize,
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.weight(1f)
            )
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = LadosTheme.colorScheme.error,
                    )
                }
            }
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
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - 5)
        }.distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && hasMoreOrders && !isLoading) {
                    onLoadMore()
                }
            }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
    ) {
        items(items = orders, key = { it.orderId }) { order ->
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
                    text = "#${order.orderId}",
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