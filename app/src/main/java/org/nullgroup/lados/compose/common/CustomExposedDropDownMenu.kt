package org.nullgroup.lados.compose.common

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuItemColors
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.R
import org.nullgroup.lados.viewmodels.customer.profile.address.MenuItemsUIState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CustomExposedDropDownMenu(
    modifier: Modifier = Modifier,
    itemsUiState: MenuItemsUIState,
    scrollState: ScrollState = rememberScrollState(),
    placeHolder: String = "",
    onItemSelected: (String, Int) -> Unit = { _, _ -> },
    currentItem: String = ""
) {
    var windowWidth = LocalConfiguration.current.screenWidthDp
    Log.d("ExposedDropDown", "${windowWidth}")
    var menuHeight by remember { mutableIntStateOf(62) }

    var isExpanded by remember { mutableStateOf(false) }
    val selectedItem by rememberUpdatedState(newValue = currentItem)
    Log.d(
        "AddressExposedDropDownMenu",
        "items: $itemsUiState \n isExpanded: $isExpanded \n currentItem: $currentItem"
    )

    ExposedDropdownMenuBox(
        modifier = modifier.widthIn(min = windowWidth.dp),
        expanded = isExpanded,
        onExpandedChange = {
            isExpanded = !isExpanded
            Log.d("AddressExposedDropDownMenu", "isExpanded: $isExpanded")
        }) {
        org.nullgroup.lados.compose.SignIn.CustomTextField(
            label = placeHolder,
            text = selectedItem,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            isReadonly = true
        )
        ExposedDropdownMenu(
            modifier = Modifier
                .widthIn(min = windowWidth.dp)
                .height(menuHeight.dp),
            scrollState = scrollState,
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            when (itemsUiState) {
                is MenuItemsUIState.Default -> {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.dropdown_no_items), color = Color.Black) },
                        onClick = { /* No action */ },
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
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            )
                        },
                        onClick = { /* No action */ },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }

                is MenuItemsUIState.Failed -> {
                    DropdownMenuItem(
                        text = {
                            LoadOnError(
                                content = {
                                    Text(text = itemsUiState.message, color = Color.Black)
                                },
                            )
                        },
                        onClick = { /* No action */ },
                    )
                }

                is MenuItemsUIState.Success -> {
                    menuHeight = (itemsUiState.data.size * 56).coerceAtMost(192)
                    LazyColumn(
                        modifier = Modifier
                            .width(windowWidth.dp)
                            .height(menuHeight.dp)
                    ) {
                        itemsIndexed(itemsUiState.data) { index, value ->
                            val isSelected = value == selectedItem
                            DropdownMenuItem(
                                modifier = Modifier
                                    .widthIn(min = windowWidth.dp)
                                    .background(
                                        if (isSelected) Color.Gray.copy(alpha = 0.1f) else Color.Transparent
                                    ),
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

@Preview(showBackground = true)
@Composable
fun AddressExposedDropDownMenuPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        CustomExposedDropDownMenu(
            placeHolder = "Province",
            itemsUiState = MenuItemsUIState.Success(listOf("123", "123", "123")),
        )
    }
}