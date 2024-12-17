package org.nullgroup.lados.compose.profile

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Address
import org.nullgroup.lados.viewmodels.customer.MenuItemsUIState

@Composable
fun AddressForm(
    modifier: Modifier = Modifier,
    provincesUiState: MenuItemsUIState,
    districtsUiState: MenuItemsUIState,
    wardsUiState: MenuItemsUIState,
    onProvinceSelected: (Int) -> Unit = {},
    onDistrictSelected: (Int) -> Unit = {},
    onWardSelected: (Int) -> Unit = {},
    onDetailChanged: (String) -> Unit = {},
    address: Address,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    Log.d("AddressForm", "Address: $address")

    var streetDetail by remember { mutableStateOf(address.detail) }
    LaunchedEffect(address) {
        streetDetail = address.detail
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddressExposedDropDownMenu(
            modifier = Modifier,
            itemsUiState = provincesUiState,
            placeHolder = "Province",
            onItemSelected = { _, index -> scope.launch { onProvinceSelected(index) } },
            currentItem = address.province
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            AddressExposedDropDownMenu(
                modifier = Modifier.weight(1f),
                itemsUiState = districtsUiState,
                placeHolder = "District",
                onItemSelected = { _, index -> scope.launch { onDistrictSelected(index) } },
                currentItem = address.district
            )
            Spacer(modifier = Modifier.width(8.dp))
            AddressExposedDropDownMenu(
                modifier = Modifier.weight(1f),
                itemsUiState = wardsUiState,
                placeHolder = "Ward",
                onItemSelected = { _, index -> scope.launch { onWardSelected(index) } },
                currentItem = address.ward
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = streetDetail,
            onValueChange = {
                onDetailChanged(it)
                streetDetail = it },
            header = "Street",
            placeHolder = "Street"
        )
    }
}

@Preview
@Composable
fun AddressFormPreview() {
    AddressForm(
        provincesUiState = MenuItemsUIState.Loading,
        districtsUiState = MenuItemsUIState.Loading,
        wardsUiState = MenuItemsUIState.Loading,
        address = Address()
    )
}