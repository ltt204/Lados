package org.nullgroup.lados.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.toDateTimeString
import org.nullgroup.lados.viewmodels.customer.order.OrderDetailState
import org.nullgroup.lados.viewmodels.customer.order.OrderDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    orderDetailViewModel: OrderDetailViewModel = hiltViewModel(),
) {
    val uiState by orderDetailViewModel.orderDetailState.collectAsState()

    when (uiState) {
        is OrderDetailState.Error -> {}
        OrderDetailState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        is OrderDetailState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Order #${
                                (uiState as OrderDetailState.Success).currentOrder.orderId.take(
                                    8
                                )
                            }"
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigateUp()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    }
                )

                StatusTimeline((uiState as OrderDetailState.Success).currentOrder.orderStatusLog)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(LadosTheme.size.medium)
                        .clickable(onClick = {})
                ) {
                    Column(modifier = Modifier.padding(LadosTheme.size.medium)) {
                        Text(
                            "Products",
                            style = LadosTheme.typography.headlineSmall,
                        )

                        Spacer(modifier = Modifier.height(LadosTheme.size.small))

                        Text("${(uiState as OrderDetailState.Success).currentOrder.orderProducts.size} items")
                        Text(
                            "Total: $${(uiState as OrderDetailState.Success).currentOrder.orderTotal}",
                            fontWeight = FontWeight.Bold
                        )

                        Divider(modifier = Modifier.padding(vertical = LadosTheme.size.small))

                        Text(
                            text = "Tap to view product details",
                            style = LadosTheme.typography.bodySmall,
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Delivery Details",
                            style = LadosTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Address:")
                        Text(
                            (uiState as OrderDetailState.Success).currentOrder.deliveryAddress,
                            style = LadosTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Phone:")
                        Text(
                            (uiState as OrderDetailState.Success).currentOrder.customerPhoneNumber,
                            style = LadosTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusTimeline(
    statusLog: Map<String, Long>,
    modifier: Modifier = Modifier,
) {
    val sortedStatuses = remember(statusLog) {
        statusLog.entries
            .sortedBy { it.value }
            .map {
                TimelineItem(
                    status = OrderStatus.valueOf(it.key),
                    timestamp = it.value
                )
            }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            sortedStatuses.forEachIndexed { index, item ->
                TimelineEntry(
                    status = item.status,
                    timestamp = item.timestamp,
                    isLast = index == sortedStatuses.lastIndex
                )
            }
        }
    }
}

@Composable
fun TimelineEntry(
    status: OrderStatus,
    timestamp: Long,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier.width(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(LadosTheme.colorScheme.primary, CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(LadosTheme.colorScheme.primary)
                )
            }
        }

        Column(modifier = Modifier.padding(start = 8.dp, bottom = if (isLast) 0.dp else 16.dp)) {
            Text(
                text = status.name,
                style = LadosTheme.typography.titleSmall,
            )
            Text(
                text = timestamp.toDateTimeString("dd/MM/yy HH:mm"),
                style = LadosTheme.typography.bodyLarge,
            )
        }
    }
}

data class TimelineItem(
    val status: OrderStatus,
    val timestamp: Long,
)