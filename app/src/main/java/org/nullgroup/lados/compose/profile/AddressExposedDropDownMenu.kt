package org.nullgroup.lados.compose.profile

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.viewmodels.customer.MenuItemsUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressExposedDropDownMenu(
    modifier: Modifier = Modifier,
    itemsUiState: MenuItemsUIState,
    scrollState: ScrollState = rememberScrollState(),
    placeHolder: String = "",
    onItemSelected: (String, Int) -> Unit = { _, _ -> },
    currentItem: String = ""
) {
    var menuHeight by remember { mutableIntStateOf(62) }

    var isExpanded by remember { mutableStateOf(false) }
    val selectedItem by rememberUpdatedState(newValue = currentItem)
    Log.d(
        "AddressExposedDropDownMenu",
        "items: $itemsUiState \n isExpanded: $isExpanded \n currentItem: $currentItem"
    )

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = isExpanded,
        onExpandedChange = {
            isExpanded = !isExpanded
            Log.d("AddressExposedDropDownMenu", "isExpanded: $isExpanded")
        }) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = selectedItem,
            label = { Text(text = placeHolder) },
            placeholder = { Text(text = placeHolder, color = Color.Gray) },
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            singleLine = true
        )
        ExposedDropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .height(menuHeight.dp),
            scrollState = scrollState,
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            when (itemsUiState) {
                is MenuItemsUIState.Default -> {
                    DropdownMenuItem(
                        text = { Text(text = "No items") },
                        onClick = { /* No action */ },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }

                is MenuItemsUIState.Loading -> {
                    DropdownMenuItem(
                        text = {
                            LoadOnProgress(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                content = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(30.dp),
                                        color = Color.Gray
                                    )
                                }
                            )
                        },
                        onClick = { /* No action */ },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }

                is MenuItemsUIState.Failed -> LoadOnError()
                is MenuItemsUIState.Success -> {
                    if (itemsUiState.data.isEmpty()) {
                        Text(text = "No items")
                    } else {
                        menuHeight = (itemsUiState.data.size * 48).coerceAtMost(192)
                        LazyColumn(
                            modifier = Modifier
                                .width(400.dp)
                                .height(300.dp)
                        ) {
                            itemsIndexed(itemsUiState.data) { index, value ->
                                val isSelected = value == selectedItem
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) Color.Gray.copy(alpha = 0.1f) else Color.Transparent
                                        )
                                        .fillMaxWidth(),
                                    text = { Text(text = value) },
                                    onClick = {
                                        onItemSelected(value, index)
                                        isExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    colors = MenuItemColors(
                                        textColor = Color.Black,
                                        leadingIconColor = Color.Black,
                                        trailingIconColor = Color.Black,
                                        disabledTextColor = Color.Gray,
                                        disabledLeadingIconColor = Color.Gray,
                                        disabledTrailingIconColor = Color.Gray,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddressExposedDropDownMenuPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        AddressExposedDropDownMenu(
            placeHolder = "Province",
            itemsUiState = MenuItemsUIState.Success(listOf("Hanoi", "HCM", "Da Nang")),
        )
    }
}