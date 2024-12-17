package org.nullgroup.lados.compose.order

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
                modifier = modifier.padding(horizontal = 0.dp),
                selected = selectedTabIndex == tabs.indexOf(tab),
                onClick = {
                    onTabSelected(tabs.indexOf(tab))
                },
                title = tabs[tabs.indexOf(tab)]
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderScreenTopAppBarPreview() {
    Surface {
        OrderScreenTopAppBar(
            modifier = Modifier,
            selectedTabIndex = 0,
            onTabSelected = {}
        )
    }
}