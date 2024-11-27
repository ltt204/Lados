package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import org.nullgroup.lados.compose.profile.AddressForm
import org.nullgroup.lados.compose.profile.ProfileTopAppBar
import org.nullgroup.lados.viewmodels.customer.EditAddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAddressScreen(
    modifier: Modifier = Modifier,
    addressId: String,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: EditAddressViewModel = hiltViewModel(),
) {
    var saveConfirmation by remember { mutableStateOf(false) }

    val address = viewModel.userAddress.collectAsState()

    val streetDetail = viewModel.streetDetail.collectAsState()
    val provincesUiState = viewModel.provincesUiState
    val districtsUiState = viewModel.districtsUiState
    val wardsUiState = viewModel.wardsUiState

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = paddingValues.calculateTopPadding()),
        topBar = {
            ProfileTopAppBar(onBackClick = { navController?.navigateUp() }, content = "Edit Address")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            AddressForm(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                provincesUiState = provincesUiState,
                districtsUiState = districtsUiState,
                wardsUiState = wardsUiState,
                onProvinceSelected = {
                    viewModel.onProvinceSelected(it)
                },
                onDistrictSelected = {
                    viewModel.onDistrictSelected(it)
                },
                onWardSelected = {
                    viewModel.onWardSelected(it)
                },
                onDetailChanged = {
                    viewModel.onStreetDetailChanged(it)
                },
                address = address.value,
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    saveConfirmation = true
                }) {
                Text(text = "Save")
            }
        }
    }

    if (saveConfirmation) {
        ConfirmAddressDialog(
            detailAddress = address.value.toString(),
            onDismissRequest = { saveConfirmation = false },
            confirmButton = {
                saveConfirmation = false
                navController?.navigateUp()
                viewModel.saveAddress()
            }
        )
    }
}