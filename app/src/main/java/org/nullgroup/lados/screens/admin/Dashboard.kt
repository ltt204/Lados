package org.nullgroup.lados.screens.admin

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firestore.v1.StructuredQuery
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.getDay
import org.nullgroup.lados.utilities.getMonth
import org.nullgroup.lados.viewmodels.admin.DashBoardRevenueState
import org.nullgroup.lados.viewmodels.admin.DashBoardViewModel
import org.nullgroup.lados.viewmodels.admin.OrdersUiState
import org.nullgroup.lados.viewmodels.admin.UsersUiState
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToLong
import kotlin.random.Random

fun updateList(orders: List<Order>): Pair<List<Order>, List<OrderProduct>> {
    val listProducts = mutableListOf<OrderProduct>()
    val listOrders = mutableListOf<Order>()

    orders.forEach { order ->
        order.orderProducts.forEach { orderProduct ->
            listProducts.add(orderProduct)
        }

        order.orderStatusLog.forEach { (key, value) ->
            if (key == OrderStatus.SHIPPED.name)
                listOrders.add(order)
        }
    }
    return Pair(listOrders, listProducts)
}

@SuppressLint("RememberReturnType")
@Composable
fun SalesAndProductReportScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: DashBoardViewModel = hiltViewModel(),
) {
    val ordersUiState = viewModel.ordersUIState.collectAsStateWithLifecycle().value

    val revenueByMonth by viewModel.revenue.collectAsState()

    val categories = listOf("All Categories", "Electronics", "Fashion", "Home")
    val sortOrderSalesTable = listOf("Ascending", "Descending")
    val sortBySalesTable = listOf("Product", "Category", "Units Sold", "Revenue")
    val selectedSortOrderSalesTable = remember { mutableStateOf(sortOrderSalesTable[0]) }
    val selectedSortBySalesTable = remember { mutableStateOf(sortBySalesTable[0]) }

    val sortOrderOrdersTable = listOf("Asc", "Desc")
    val sortByOrdersTable = listOf("Time", "No. orders", "Revenue")
    val groupByOrdersTable = listOf("Day", "Week", "Month")
    val selectedSortOrderOrdersTable = remember { mutableStateOf(sortOrderOrdersTable[0]) }
    val selectedSortByOrdersTable = remember { mutableStateOf(sortByOrdersTable[0]) }
    val selectedGroupByOrdersTable = remember { mutableStateOf(groupByOrdersTable[0]) }

    val sampleData = remember { generateSampleData() }

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var startDatePicker by remember { mutableStateOf<Date?>(null) }
    var endDatePicker by remember { mutableStateOf<Date?>(null) }

    val startDatePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, selectedYear, selectedMonth, selectedDay ->
            startDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"

            val _calendar = Calendar.getInstance()
            _calendar.set(selectedYear, selectedMonth, selectedDay)
            startDatePicker = _calendar.time
        },
        year,
        month,
        day
    )

    val endDatePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, selectedYear, selectedMonth, selectedDay ->
            endDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            val _calendar = Calendar.getInstance()
            _calendar.set(selectedYear, selectedMonth, selectedDay)
            endDatePicker = _calendar.time
        },
        year,
        month,
        day
    )

    when (ordersUiState) {
        is OrdersUiState.Loading -> {
            LoadOnProgress(
                modifier = modifier,
                content = { CircularProgressIndicator() }
            )
        }

        is OrdersUiState.Error -> {
            Text(
                text = "Failed to load data",
                style = LadosTheme.typography.headlineSmall.copy(
                    color = LadosTheme.colorScheme.error,
                )
            )
        }

        is OrdersUiState.Success -> {
            val orders = ordersUiState.orders

            var listProducts by remember { mutableStateOf<List<OrderProduct>>(emptyList()) }
            var listOrders by remember { mutableStateOf<List<Order>>(emptyList()) }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(horizontal = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),

                ) {
                Text(
                    "Sales and Product Report",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "View report of sales and product performance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Divider()
                Spacer(Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Row {
                        Text(
                            text = if (startDate.isEmpty() && endDate.isEmpty()) {
                                "Select Date Range"
                            } else {
                                "Selected Range: $startDate - $endDate"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { startDatePickerDialog.show() }) {
                            Text("Pick Start Date")
                        }

                        Button(onClick = { endDatePickerDialog.show() }) {
                            Text("Pick End Date")
                        }

                        IconButton(
                            modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(50)),
                            onClick = {
                                startDatePicker?.let { start ->
                                    endDatePicker?.let { end ->
                                        val filteredOrders = orders.filter { order ->
                                            val creationTimestamp = order.orderStatusLog[OrderStatus.CREATED.name]
                                            val creationDate = creationTimestamp?.let { Date(it) }
                                            creationDate != null && creationDate >= start && creationDate <= end
                                        }

                                        listProducts = filteredOrders.flatMap { it.orderProducts }
                                        listOrders = filteredOrders.filter { order ->
                                            order.orderStatusLog.containsKey(OrderStatus.SHIPPED.name)
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "search"
                            )
                        }
                    }
                }
                if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "The result is from $startDate to $endDate",
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(900.dp, 5000.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Orders", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Group By: ", fontWeight = FontWeight.Bold)
                        DropdownMenu(selectedGroupByOrdersTable, groupByOrdersTable)
                        Text("Sort By: ", fontWeight = FontWeight.Bold)
                        DropdownMenu(selectedSortByOrdersTable, sortByOrdersTable, onClick = { type ->
                            when(type){
                                "Time" -> listOrders = listOrders.sortedBy { it.orderStatusLog.entries.minBy { it.value }.value }
                                "No. orders" -> listOrders = listOrders
                                "Revenue" -> listOrders = listOrders.sortedByDescending { it.orderTotal }
                            }

                        }
                            )
                        /*
                        Text("Sort Order", fontWeight = FontWeight.Bold)
                        DropdownMenu(selectedSortOrderOrdersTable, sortOrderOrdersTable, onClick = { type ->
                            when (type) {
                                "Asc" -> listOrders = listOrders.sortedBy { it.orderStatusLog.entries.minBy { it.value }.value }
                                "Desc" -> listOrders = listOrders
                            }
                        })

                         */
                    }

                    OrderTable(data = listOrders)


                    Spacer(Modifier.width(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Revenue by Category", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    }

                    when (revenueByMonth) {
                        is DashBoardRevenueState.Loading -> {
                            Text("Loading Revenue Data")
                        }

                        is DashBoardRevenueState.Success -> {
                            val revenueByMonthSuccess =
                                (revenueByMonth as DashBoardRevenueState.Success).data
                            Log.d("Revenue", revenueByMonthSuccess.toString())
                            RevenueChart(Modifier.height(512.dp), revenueByMonthSuccess)
                        }

                        is DashBoardRevenueState.Error -> {}
                    }
                    ///////////////
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Sales Table", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Spacer(Modifier.weight(1f))
                        Text("Sort By", fontWeight = FontWeight.Bold)
                        DropdownMenu(selectedSortBySalesTable, sortBySalesTable, onClick = { type ->
                            when(type){
                                "Product" -> listProducts = listProducts.sortedByDescending { it.productId }
                                "Category" -> listProducts = listProducts.sortedByDescending { it.variantId }
                                "Units Sold" -> listProducts = listProducts.sortedByDescending { it.amount }
                                "Revenue" -> listProducts = listProducts.sortedByDescending { it.totalPrice }
                        }})
                        //Spacer(Modifier.width(8.dp))
                        //Text("Sort Order", fontWeight = FontWeight.Bold)
                        //DropdownMenu(selectedSortOrderSalesTable, sortOrderSalesTable)
                    }

                    SalesTable(data = listProducts)


                    Spacer(Modifier.width(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Revenue by Category", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    }

                    ChartPlaceholder()

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { /* Export logic */ }) {
                        Text("Export as CSV")
                    }
                    Text(
                        "Total Revenue: $${sampleData.sumOf { it.revenue }}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownMenu(
    selected: MutableState<String>,
    options: List<String>,
    onClick: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.background(
            LadosTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp)
        )
    ) {
        Text(
            selected.value,
            modifier = Modifier
                .clickable { expanded = true }
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selected.value = option
                        onClick(selected.value)
                        expanded = false
                    },
                    text = {
                        Text(option)
                    })
            }
        }
    }
}

@Composable
fun OrderTable(
    data: List<Order>,
) {
    LazyColumn(modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(8.dp))) {
        item {
            Row(
                modifier = Modifier
                    .background(LadosTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Time", Modifier.weight(1.2f))
                Text(
                    "No. orders",
                    Modifier.weight(0.5f),

                )
                Text(
                    "Revenue",
                    Modifier.weight(0.5f),
                )
            }
        }

        items(data) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1.2f),
                    text = item.orderStatusLog[OrderStatus.CREATED.name]?.let { Date(it) }.toString(),
                )
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight()
                )
                Text(
                    modifier = Modifier
                        .weight(0.5f),
                    text = "1"
                )
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight()
                )
                Text(
                    modifier = Modifier
                        .weight(0.5f),
                    text = "$${item.orderTotal}",
                )
            }
        }
    }
}


@Composable
fun SalesTable(data: List<OrderProduct>) {
    LazyColumn(modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(8.dp))) {
        item {
            Row(
                modifier = Modifier
                    .background(LadosTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Product", Modifier.weight(1.5f))
                Text("Category", Modifier.weight(1f))
                Text("Units Sold", Modifier.weight(1f))
                Text("Revenue", Modifier.weight(1f))
            }
        }

        items(data) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(item.productId, Modifier.weight(1.5f))
                Text(item.variantId, Modifier.weight(1f))
                Text(item.amount.toString(), Modifier.weight(1f))
                Text("$${item.totalPrice}", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ChartPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text("Chart Placeholder (e.g., Bar or Pie Chart)")
    }
}

@Composable
fun RevenueChart(
    modifier: Modifier = Modifier,
    revenueMap: Map<String, Double>
) {
    val primaryColor = LadosTheme.colorScheme.primary
    val listData = mutableListOf<Bars>()
    revenueMap.forEach { (mon, rev) ->
        Log.d("Revenue", "Month: $mon, Revenue: $rev")
        val revWith = ((rev*100).roundToLong().toDouble())/100
        Log.d("Revenue", "Month: $mon, Revenue: $revWith")
        listData.add(
            Bars(
                label = mon, values = listOf(
                    Bars.Data(value = revWith, color = SolidColor(primaryColor)),
                )
            )
        )
    }
    ColumnChart(
        modifier = modifier,
        data = listData,
        barProperties = BarProperties(
            spacing = 1.dp,
            thickness = 10.dp,
        )
    )
}

data class ProductData(
    val productName: String,
    val category: String,
    val unitsSold: Int,
    val revenue: Int
)

fun generateSampleData(): List<ProductData> {
    val categories = listOf("Electronics", "Fashion", "Home")
    return List(10) {
        ProductData(
            productName = "Product ${it + 1}",
            category = categories.random(),
            unitsSold = Random.nextInt(10, 100),
            revenue = Random.nextInt(500, 5000)
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun SalesAndProductReportScreenPreview() {
//    SalesAndProductReportScreen()
//}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RevenueChartPreview() {
    val revenueMap = mapOf(
        "Jan" to 1000.0,
        "Feb" to 2000.0,
        "Mar" to 3000.0,
        "Apr" to 4000.0,
        "May" to 5000.0,
        "Jun" to 6000.0,
        "Jul" to 7000.0,
        "Aug" to 8000.0,
        "Sep" to 9000.0,
        "Oct" to 10000.0,
        "Nov" to 11000.0,
        "Dec" to 12000.0,
    )
    RevenueChart(
        modifier = Modifier.height(512.dp),revenueMap = revenueMap)
}