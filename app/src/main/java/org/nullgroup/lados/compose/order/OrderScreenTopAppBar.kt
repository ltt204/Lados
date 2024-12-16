package org.nullgroup.lados.compose.order

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.capitalizeWords

@Composable
fun OrderScreenTopAppBar(
    modifier: Modifier,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = OrderStatus.entries.map { it.name.capitalizeWords() }
    Log.d("OrderScreenTopAppBar", "OrderUiState: $selectedTabIndex")
    LazyRow(
        modifier = modifier.fillMaxWidth(),
    ) {
        items(items = tabs, key = { tabs.indexOf(it) }) { tab ->
            ScrollTabItem(
                modifier = Modifier.padding(horizontal = 4.dp),
                selected = selectedTabIndex == tabs.indexOf(tab),
                onClick = {
                    onTabSelected(tabs.indexOf(tab))
                },
                title = tabs[tabs.indexOf(tab)]
            )
        }
    }
}