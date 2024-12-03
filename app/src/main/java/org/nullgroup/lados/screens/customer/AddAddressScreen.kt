package org.nullgroup.lados.screens.customer

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.compose.profile.AddressForm
import org.nullgroup.lados.compose.profile.ConfirmDialog
import org.nullgroup.lados.compose.profile.ProfileTopAppBar
import org.nullgroup.lados.viewmodels.customer.AddAddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: AddAddressViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    var saveConfirmation by remember { mutableStateOf(false) }
    var cancelConfirmation by remember { mutableStateOf(false) }

    val userAddress = viewModel.userAddress.collectAsState()
    val provincesUiState = viewModel.provincesUiState
    val districtsUiState = viewModel.districtsUiState
    val wardsUiState = viewModel.wardsUiState

    BackHandler {
        cancelConfirmation = true
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = paddingValues.calculateTopPadding()),
        topBar = {
            ProfileTopAppBar(onBackClick = {
                cancelConfirmation = true
            }, content = "Address")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
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
                    address = userAddress.value,
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.isInfoChanged.value,
                onClick = {
                    saveConfirmation = true
                }) {
                Text(text = "Save")
            }
        }
    }

    if (saveConfirmation) {
        ConfirmDialog(
            title = { Text(text = "Confirm Address") },
            message = { Text(text = userAddress.value.toString()) },
            onDismissRequest = { saveConfirmation = false },
            confirmButton = {
                saveConfirmation = false
                navController?.navigateUp()
                viewModel.saveAddress()
            }
        )
    }

    if (cancelConfirmation && viewModel.isInfoChanged.value) {
        ConfirmDialog(
            title = { Text(text = "Confirm Cancel") },
            message = { Text(text = "Are you sure you want to cancel?") },
            onDismissRequest = { cancelConfirmation = false },
            confirmButton = {
                cancelConfirmation = false
                navController?.navigateUp()
            },
            primaryButtonText = "Exit",
            secondaryButtonText = "Continue"
        )
    } else if (cancelConfirmation) {
        cancelConfirmation = false
        navController?.navigateUp()
    }
}
