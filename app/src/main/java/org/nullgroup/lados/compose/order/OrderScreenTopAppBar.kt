package org.nullgroup.lados.compose.order

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
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

    LazyRow(
        modifier = modifier.fillMaxWidth(),
    ) {
        items(items = tabs) { tab ->
            val index = tabs.indexOf(tab)
            ScrollTabItem(
                modifier = Modifier.padding(horizontal = 4.dp),
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                title = tabs[index]
            )
        }
    }
}