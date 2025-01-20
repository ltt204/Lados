package org.nullgroup.lados.screens.admin.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.nullgroup.lados.screens.common.LoadingScreen
import org.nullgroup.lados.screens.customer.home.ProductItem
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.admin.inventory.InventoryTrackingViewModel

@Composable
fun InventoryTracking(
    viewModel: InventoryTrackingViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    Scaffold (
        modifier = modifier.fillMaxSize()
            .background(LadosTheme.colorScheme.background)
            .padding(paddingValues),
    ){  it -> val padding = it

        var selectedTab by remember { mutableStateOf(0) }
        val products by viewModel.inventoryItems.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val isLoadingAll by viewModel.isLoadingAll.collectAsState()

        LaunchedEffect(selectedTab){
            viewModel.loadProducts(selectedTab + 1)
        }

        Column(
           modifier = Modifier.fillMaxSize()
               .background(LadosTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            InventoryTabRow(
                selectedTabIndex = selectedTab,
                onTabSelected = { index -> selectedTab = index }
            )

            if(isLoadingAll){
               Box(
                   modifier = Modifier.fillMaxSize()
                       .background(LadosTheme.colorScheme.background),
                   contentAlignment = Alignment.Center
               ){
                   CircularProgressIndicator(
                       color = LadosTheme.colorScheme.primary
                   )
               }
            } else {
                Text(
                    text = "Num: ${products.size}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LadosTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(products.size) {
                        org.nullgroup.lados.screens.admin.product.ProductItem(
                            product = products[it]
                        )
                    }
                }
            }
        }
    }
}

val tabColors = listOf(
    Color(0xFF4CAF50), // Green for In Stock
    Color(0xFFFFC107), // Yellow for Low Stock
    Color(0xFFF44336)  // Red for Out of Stock
)

@Composable
fun InventoryTabRow(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("In Stock", "Low Stock", "Out of Stock")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = LadosTheme.colorScheme.surfaceContainerHighest,
        contentColor = LadosTheme.colorScheme.onSurface,
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        color = if (selectedTabIndex == index) Color.White else tabColors[index],
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.background(
                    if (selectedTabIndex == index) tabColors[index] else Color.Transparent
                )
            )
        }
    }
}



