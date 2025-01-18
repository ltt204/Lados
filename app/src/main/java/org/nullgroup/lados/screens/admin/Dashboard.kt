package org.nullgroup.lados.screens.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.app.DatePickerDialog
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.nullgroup.lados.screens.admin.userManagement.SearchBar
import org.nullgroup.lados.ui.theme.LadosTheme
import java.time.LocalDate
import java.util.Calendar
import kotlin.random.Random

@SuppressLint("RememberReturnType")
@Composable
fun SalesAndProductReportScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    //viewModel: UserManagementViewModel = hiltViewModel(),
    //filterState: FilterState = FilterState(),
) {
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

    val startDatePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, selectedYear, selectedMonth, selectedDay ->
            startDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year,
        month,
        day
    )

    val endDatePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, selectedYear, selectedMonth, selectedDay ->
            endDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year,
        month,
        day
    )

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
                    }
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
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "search"
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = if (startDate.isNotEmpty() && endDate.isNotEmpty()) "The result is from $startDate to $endDate" else "",
                fontStyle = FontStyle.Italic
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().heightIn(900.dp,5000.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Orders", fontWeight = FontWeight.Bold, fontSize = 28.sp)
            }
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("Group By", fontWeight = FontWeight.Bold)
                DropdownMenu(selectedGroupByOrdersTable, groupByOrdersTable)
                Text("Sort By", fontWeight = FontWeight.Bold)
                DropdownMenu(selectedSortByOrdersTable, sortByOrdersTable)
                Text("Sort Order", fontWeight = FontWeight.Bold)
                DropdownMenu(selectedSortOrderOrdersTable, sortOrderOrdersTable)
            }

            OrderTable(data = sampleData)


            Spacer(Modifier.width(16.dp))

            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Revenue by Category", fontWeight = FontWeight.Bold, fontSize = 28.sp)
            }

            ChartPlaceholder()

            ///////////////
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Sales Table", fontWeight = FontWeight.Bold, fontSize = 28.sp)
            }
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Spacer(Modifier.weight(1f))
                Text("Sort By", fontWeight = FontWeight.Bold)
                DropdownMenu(selectedSortBySalesTable, sortBySalesTable)
                Spacer(Modifier.width(8.dp))
                Text("Sort Order", fontWeight = FontWeight.Bold)
                DropdownMenu(selectedSortOrderSalesTable, sortOrderSalesTable)
            }

            SalesTable(data = sampleData)


            Spacer(Modifier.width(16.dp))

            Row(
                modifier=Modifier.fillMaxWidth(),
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


@Composable
fun DropdownMenu(selected: MutableState<String>, options: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    Box {
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
fun OrderTable(data: List<ProductData>) {
    LazyColumn(modifier = Modifier.border(1.dp, Color.Black)) {
        item {
            Row(
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Time", Modifier.weight(1.2f))
                Text("No. orders", Modifier.weight(0.5f))
                Text("Revenue", Modifier.weight(0.5f))
            }
        }

        items(data) { item ->
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(item.productName, Modifier.weight(1.2f))
                Text(item.unitsSold.toString(), Modifier.weight(0.5f))
                Text("$${item.revenue}", Modifier.weight(0.5f))
            }
        }
    }
}


@Composable
fun SalesTable(data: List<ProductData>) {
    LazyColumn(modifier = Modifier.border(1.dp, Color.Black)) {
        item {
            Row(
                modifier = Modifier
                    .background(Color.Gray)
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
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(item.productName, Modifier.weight(1.5f))
                Text(item.category, Modifier.weight(1f))
                Text(item.unitsSold.toString(), Modifier.weight(1f))
                Text("$${item.revenue}", Modifier.weight(1f))
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SalesAndProductReportScreenPreview() {
    SalesAndProductReportScreen()
}