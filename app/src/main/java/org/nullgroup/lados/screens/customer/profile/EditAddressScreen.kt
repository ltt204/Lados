package org.nullgroup.lados.screens.customer.profile

import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.compose.common.ProfileTopAppBar
import org.nullgroup.lados.compose.profile.AddressForm
import org.nullgroup.lados.compose.profile.ConfirmDialog
import org.nullgroup.lados.viewmodels.customer.EditAddressViewModel
import org.nullgroup.lados.viewmodels.customer.SavingResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAddressScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: EditAddressViewModel = hiltViewModel(),
) {
    var saveConfirmation by remember { mutableStateOf(false) }
    var cancelConfirmation by remember { mutableStateOf(false) }

    val userAddress = viewModel.userAddress.collectAsState()

    val provincesUiState = viewModel.provincesUiState
    val districtsUiState = viewModel.districtsUiState
    val wardsUiState = viewModel.wardsUiState
    var isSaveClick by remember {
        mutableStateOf(false)
    }
    BackHandler {
        cancelConfirmation = true
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = paddingValues.calculateTopPadding()),
        topBar = {
            ProfileTopAppBar(
                onBackClick = {
                    cancelConfirmation = true
                },
                content = "Edit Address"
            )
        },
        containerColor = Color.Transparent
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
                address = userAddress.value,
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    saveConfirmation = true
                },
                enabled = viewModel.isInfoChanged.value
            ) {
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
                viewModel.saveAddress()
                isSaveClick = true
            }
        )
    }

    if (isSaveClick) {
        when (viewModel.savingResult.value) {
            is SavingResult.Loading -> {
                // Do nothing
            }

            is SavingResult.Success -> {
                isSaveClick = false
                navController?.navigateUp()
            }

            is SavingResult.Failed -> {
                Toast.makeText(
                    navController?.context,
                    (viewModel.savingResult.value as SavingResult.Failed).message,
                    Toast.LENGTH_SHORT
                ).show()
                isSaveClick = false
            }
        }
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