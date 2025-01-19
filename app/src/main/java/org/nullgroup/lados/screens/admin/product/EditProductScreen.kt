package org.nullgroup.lados.screens.admin.product

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.profile.LoadingContent
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.admin.product.EditProductScreenViewModel
import org.nullgroup.lados.viewmodels.admin.product.EditProductUiState
import org.nullgroup.lados.viewmodels.admin.product.validateEmpty
import org.nullgroup.lados.viewmodels.admin.product.validateVariants

@Composable
fun EditProductScreen(
    modifier: Modifier = Modifier,
    productId: String,
    navController: NavController,
    viewModel: EditProductScreenViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    Log.d("ProductItem", "Product ID: $productId")

    val scrollState = rememberScrollState()
    val productVariants = viewModel.productVariants.collectAsState()
    Log.d("AddProductScreen", "Current Product variants: ${productVariants.value.size}")
    val categories by viewModel.categories.collectAsState()

    val productUiState = viewModel.productUiState.value
    val currentProduct by viewModel.productZombie.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()

    Log.d("Test current product", "Current product: $currentProduct")

    LaunchedEffect(key1 = Unit) {
        viewModel.loadProduct(productId)
        viewModel.getCategories()
    }

    var confirmUpdate by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = updateSuccess){
        if(updateSuccess){
            viewModel.clearProductVariants()
            viewModel.clearProductZombie()
            navController.navigateUp()

        }
    }


//    LaunchedEffect(key1 = productUiState) {
//        if(productUiState is ProductUiState.Success){
//            viewModel.clearProductVariants()
//            viewModel.clearProductZombie()
//            navController.navigateUp()
//        }
//    }

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
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
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
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = {
                        confirmUpdate = true
                    }, shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LadosTheme.colorScheme.primary,
                        contentColor = LadosTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Update",
                        style = LadosTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    ) { it ->

        when (productUiState) {
            is EditProductUiState.Loading -> {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background)
                )
            }

            is EditProductUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(LadosTheme.colorScheme.background),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ){
                    Text(
                        text = productUiState.message,
                        style = LadosTheme.typography.titleMedium,
                        color = LadosTheme.colorScheme.error
                    )
                }

            }

            is EditProductUiState.Success -> {

                val focusManage = LocalFocusManager.current

                var name by rememberSaveable {
                    mutableStateOf(
                        mapOf(
                            "vi" to currentProduct.name["vi"].toString(),
                            "en" to currentProduct.name["en"].toString()
                        )
                    )
                }

                var description by rememberSaveable {
                    mutableStateOf(
                        mapOf(
                            "vi" to currentProduct.description["vi"].toString(),
                            "en" to currentProduct.description["en"].toString()
                        )
                    )
                }

                var selectedCategory by remember {
                    mutableStateOf(
                        Category()
                        //categories.first { it.categoryId == currentProduct.categoryId }
                    )
                }

                var viNameError by remember { mutableStateOf(Pair(true, "")) }
                var enNameError by remember { mutableStateOf(Pair(true, "")) }
                var viDescriptionError by remember { mutableStateOf(Pair(true, "")) }
                var enDescriptionError by remember { mutableStateOf(Pair(true, "")) }
                var variantError by remember { mutableStateOf(Pair(true, "")) }
                var categoryError by remember { mutableStateOf(Pair(true, "")) }

                var viNameFocus by remember { mutableStateOf(false) }
                var enNameFocus by remember { mutableStateOf(false) }
                var viDescriptionFocus by remember { mutableStateOf(false) }
                var enDescriptionFocus by remember { mutableStateOf(false) }

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (!viNameFocus) return@onFocusChanged
                                if (!it.isFocused) viNameError = validateEmpty(name["vi"] ?: "")
                            },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManage.clearFocus()
                                viNameError = validateEmpty(name["vi"] ?: "")
                            }
                        ),
                        isError = !viNameError.first
                    )

                    if (!viNameError.first) {
                        Text(
                            text = viNameError.second,
                            style = LadosTheme.typography.bodySmall,
                            color = LadosTheme.colorScheme.error
                        )
                    }

                    CustomTextField(
                        label = "English Name",
                        text = name["en"] ?: "",
                        onValueChange = {
                            name = name.toMutableMap().apply { this["en"] = it }
                            viewModel.onNameChanged(name)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (!enNameFocus) return@onFocusChanged
                                if (!it.isFocused) enNameError = validateEmpty(name["en"] ?: "")
                            },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManage.clearFocus()
                                enNameError = validateEmpty(name["en"] ?: "")
                            }
                        ),
                        isError = !enNameError.first,

                        )

                    if (!enNameError.first) {
                        Text(
                            text = enNameError.second,
                            style = LadosTheme.typography.bodySmall,
                            color = LadosTheme.colorScheme.error
                        )
                    }

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (!viDescriptionFocus) return@onFocusChanged
                                if (!it.isFocused) viDescriptionError =
                                    validateEmpty(description["vi"] ?: "")
                            },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManage.clearFocus()
                                viDescriptionError = validateEmpty(description["vi"] ?: "")
                            }),
                        isError = !viDescriptionError.first
                    )

                    if (!viDescriptionError.first) {
                        Text(
                            text = viDescriptionError.second,
                            style = LadosTheme.typography.bodySmall,
                            color = LadosTheme.colorScheme.error
                        )
                    }

                    CustomTextField(
                        label = "English Description",
                        text = description["en"] ?: "",
                        onValueChange = {
                            description = description.toMutableMap().apply { this["en"] = it }
                            viewModel.onDescriptionChanged(description)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (!enDescriptionFocus) return@onFocusChanged
                                if (!it.isFocused) enDescriptionError =
                                    validateEmpty(description["en"] ?: "")
                            },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManage.clearFocus()
                                enDescriptionError = validateEmpty(description["en"] ?: "")
                            }),
                        isError = !enDescriptionError.first
                    )

                    if (!enDescriptionError.first) {
                        Text(
                            text = enDescriptionError.second,
                            style = LadosTheme.typography.bodySmall,
                            color = LadosTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.size(4.dp))

                    Text(
                        text = "Category",
                        style = LadosTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LadosTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenuWithTextField(
                        label = "Choose category",
                        options = categories,
                        displayName = {
                            it.categoryName
                        },
                        selectedOption = selectedCategory,
                        onOptionSelected = {
                            selectedCategory = it
                            categoryError = validateEmpty(it.categoryName)
                            viewModel.onCategoryChanged(it.categoryId)
                        }
                    )

                    if (!categoryError.first) {
                        Text(
                            text = categoryError.second,
                            style = LadosTheme.typography.bodySmall,
                            color = LadosTheme.colorScheme.error
                        )
                    }

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
                                navController.navigate("add_variant/${currentProduct.id}")
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

                    if (!variantError.first) {
                        Text(
                            text = variantError.second,
                            style = LadosTheme.typography.bodySmall,
                            color = LadosTheme.colorScheme.error
                        )
                    }

                    VariantsSection(
                        productVariants.value,
                        isEditable = true,
                        navController = navController,
                    )
                }

                if (confirmUpdate) {

                    viNameError = validateEmpty(name["vi"] ?: "")
                    enNameError = validateEmpty(name["en"] ?: "")
                    viDescriptionError = validateEmpty(description["vi"] ?: "")
                    enDescriptionError = validateEmpty(description["en"] ?: "")
                    variantError = validateVariants(currentProduct.variants)
                    categoryError = validateEmpty(selectedCategory.categoryName)

                    if (
                        viNameError.first &&
                        enNameError.first &&
                        viDescriptionError.first &&
                        enDescriptionError.first &&
                        variantError.first
                    ) {
                        viewModel.onUpdateProductButtonClick()
                    }
                }
            }
        }
    }
}
