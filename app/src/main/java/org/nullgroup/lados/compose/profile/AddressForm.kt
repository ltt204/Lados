package org.nullgroup.lados.compose.profile

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.SignIn.CustomTextField
import org.nullgroup.lados.compose.common.CustomExposedDropDownMenu
import org.nullgroup.lados.data.models.Address
import org.nullgroup.lados.viewmodels.customer.profile.address.MenuItemsUIState

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
        CustomExposedDropDownMenu(
            modifier = Modifier,
            itemsUiState = provincesUiState,
            placeHolder = stringResource(R.string.address_province),
            onItemSelected = { _, index -> scope.launch { onProvinceSelected(index) } },
            currentItem = address.province
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomExposedDropDownMenu(
                modifier = Modifier.weight(1f),
                itemsUiState = districtsUiState,
                placeHolder = stringResource(R.string.address_district),
                onItemSelected = { _, index -> scope.launch { onDistrictSelected(index) } },
                currentItem = address.district
            )
            Spacer(modifier = Modifier.width(8.dp))
            CustomExposedDropDownMenu(
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
                .fillMaxWidth()
                .wrapContentHeight()
                .heightIn(max = 128.dp),
            text = streetDetail,
            onValueChange = {
                onDetailChanged(it)
                streetDetail = it
            },
            label = stringResource(R.string.address_street),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddressFormPreview() {
    AddressForm(
        provincesUiState = MenuItemsUIState.Loading,
        districtsUiState = MenuItemsUIState.Loading,
        wardsUiState = MenuItemsUIState.Loading,
        address = Address()
    )
}