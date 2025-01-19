package org.nullgroup.lados.screens.admin.product

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.admin.product.AddProductScreenViewModel

@Composable
fun EditProductScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AddProductScreenViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {

    val scrollState = rememberScrollState()
    val currentProductId = viewModel.currentProductId.collectAsState()
    val productVariants = viewModel.productVariants.collectAsState()
    Log.d("AddProductScreen", "Current Product variants: ${productVariants.value.size}")

    var name by rememberSaveable {
        mutableStateOf(
            mapOf(
                "vi" to "",
                "en" to ""
            )
        )
    }

    var description by rememberSaveable {
        mutableStateOf(
            mapOf(
                "vi" to "",
                "en" to ""
            )
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(LadosTheme.colorScheme.background),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1f).height(48.dp),
                    onClick = { navController.navigateUp() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LadosTheme.colorScheme.error,
                        contentColor = LadosTheme.colorScheme.onError
                    )
                ) {
                    Text(text = "Cancel")
                }

                Button(
                    modifier = Modifier.weight(1f).height(48.dp),
                    onClick = {
                        viewModel.onAddProductButtonClick()
                    }, shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LadosTheme.colorScheme.primary,
                        contentColor = LadosTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Add",
                        style = LadosTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    ) { it ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LadosTheme.colorScheme.background)
                .padding(it)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {

            Text(
                text = "Name",
                style = LadosTheme.typography.titleMedium,
                color = LadosTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            CustomTextField(
                label = "Vietnamese Name",
                text = name["vi"] ?: "",
                onValueChange = {
                    name = name.toMutableMap().apply { this["vi"] = it }
                    viewModel.onNameChanged(name)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                isError = false
            )

            CustomTextField(
                label = "English Name",
                text = name["en"] ?: "",
                onValueChange = {
                    name = name.toMutableMap().apply { this["en"] = it }
                    viewModel.onNameChanged(name)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                isError = false
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = "Description",
                style = LadosTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LadosTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            CustomTextField(
                label = "Vietnamese Description",
                text = description["vi"] ?: "",
                onValueChange = {
                    description = description.toMutableMap().apply { this["vi"] = it }
                    viewModel.onDescriptionChanged(description)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                isError = false
            )

            CustomTextField(
                label = "English Description",
                text = description["en"] ?: "",
                onValueChange = {
                    description = description.toMutableMap().apply { this["en"] = it }
                    viewModel.onDescriptionChanged(description)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                isError = false
            )

            Spacer(modifier = Modifier.size(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Variants",
                    style = LadosTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LadosTheme.colorScheme.onBackground,
                )

                TextButton(
                    onClick = {
                        viewModel.createBlankProduct()
                        navController.navigate("add_variant/${currentProductId.value}")
                    }

                ) {
                    Text(
                        text = "+ Add",
                        style = LadosTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LadosTheme.colorScheme.primary,
                    )
                }
            }

            VariantsSection(productVariants.value)
        }
    }
}