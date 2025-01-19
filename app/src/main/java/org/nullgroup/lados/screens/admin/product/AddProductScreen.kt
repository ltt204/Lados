package org.nullgroup.lados.screens.admin.product

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.remote.models.ProductVariantRemoteModel
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.admin.product.AddProductScreenViewModel
import org.nullgroup.lados.viewmodels.admin.product.ProductUiState
import org.nullgroup.lados.viewmodels.admin.product.validateEmpty
import org.nullgroup.lados.viewmodels.admin.product.validateVariants


@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AddProductScreenViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {

    val scrollState = rememberScrollState()
    val currentProductId = viewModel.currentProductId.collectAsState()
    val productVariants = viewModel.productVariants.collectAsState()
    Log.d("AddProductScreen", "Current Product variants: ${productVariants.value.size}")
    val productUiState = viewModel.productUiState.value
    val categories by viewModel.categories.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getCategories()
    }

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

    var selectedCategory by remember { mutableStateOf(Category()) }

    var viNameError by remember { mutableStateOf(Pair(true, "")) }
    var enNameError by remember { mutableStateOf(Pair(true, "")) }
    var viDescriptionError by remember { mutableStateOf(Pair(true, "")) }
    var enDescriptionError by remember { mutableStateOf(Pair(true, "")) }
    var variantError by remember { mutableStateOf(Pair(true, "")) }
    var categoryError by remember { mutableStateOf(Pair(true, "")) }

    var viNameFocus by remember { mutableStateOf(false) }
    var enNameFocus by remember  { mutableStateOf(false) }
    var viDescriptionFocus by remember  { mutableStateOf(false) }
    var enDescriptionFocus by remember  { mutableStateOf(false) }


    LaunchedEffect(key1 = productUiState) {
        if(productUiState is ProductUiState.Success){
            viewModel.clearProductVariants()
            viewModel.clearProductZombie()
            navController.navigateUp()
        }
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

                        viNameError = validateEmpty(name["vi"] ?: "")
                        enNameError = validateEmpty(name["en"] ?: "")
                        viDescriptionError = validateEmpty(description["vi"] ?: "")
                        enDescriptionError = validateEmpty(description["en"] ?: "")
                        variantError = validateVariants(productVariants.value)
                        categoryError = validateEmpty(selectedCategory.categoryName)

                        if(
                            viNameError.first &&
                            enNameError.first &&
                            viDescriptionError.first &&
                            enDescriptionError.first &&
                            variantError.first
                        ){
                            viewModel.onAddProductButtonClick()
                        }

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

        val focusManage = LocalFocusManager.current

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
                modifier = Modifier.fillMaxWidth()
                    .onFocusChanged {
                        if (!viNameFocus) return@onFocusChanged
                        if (!it.isFocused) viNameError = validateEmpty(name["vi"] ?: "")
                    },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
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
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
                    .onFocusChanged {
                        if (!viDescriptionFocus) return@onFocusChanged
                        if (!it.isFocused) viDescriptionError = validateEmpty(description["vi"] ?: "")
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
                    text =viDescriptionError.second,
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
                modifier = Modifier.fillMaxWidth()
                    .onFocusChanged {
                        if (!enDescriptionFocus) return@onFocusChanged
                        if (!it.isFocused) enDescriptionError = validateEmpty(description["en"] ?: "")
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

            if(!variantError.first){
                Text(
                    text = variantError.second,
                    style = LadosTheme.typography.bodySmall,
                    color = LadosTheme.colorScheme.error
                )
            }

            VariantsSection(productVariants.value)
        }
    }
}


@Composable
fun VariantsSection(
    variants: List<ProductVariantRemoteModel> = emptyList(),
    modifier: Modifier = Modifier
) {
    Log.d("Variants", "Variants: $variants")
    LazyColumn(
        modifier = modifier.height(200.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    )
    {
        items(variants.size) {
            val variant = variants[it]
            VariantItem(variant = variant)
        }
    }

}

@Composable
fun VariantItem(
    variant: ProductVariantRemoteModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, LadosTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = LadosTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Thêm khoảng cách
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Khoảng cách giữa hình ảnh và thông tin
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hình ảnh variant
            SubcomposeAsyncImage(
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                loading = {
                    LoadOnProgress(
                        modifier = Modifier
                            .clip(CircleShape)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    }
                },
                model = ImageRequest
                    .Builder(context = LocalContext.current)
                    .data(variant.images.first().link)
                    .crossfade(true)
                    .build(),
                contentDescription = "Variant Image"
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Color: ${variant.color.colorName["en"]}",
                    color = LadosTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp)) // Khoảng cách giữa các dòng
                Text(
                    text = "Size: ${variant.size.sizeName["en"]}",
                    color = LadosTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Price: ${variant.originalPrice["en"]}",
                    color = LadosTheme.colorScheme.onBackground,
                    textDecoration = TextDecoration.LineThrough
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sale Price: ${variant.salePrice?.get("en")}",
                    color = LadosTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Quantity: ${variant.quantityInStock}",
                    color = LadosTheme.colorScheme.onBackground
                )
            }
        }
    }
}


@Composable
fun <T> DropdownMenuWithTextField(
    label: String,
    options: List<T>,
    displayName: (T) -> String, // Hàm để hiển thị tên của mỗi item
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.wrapContentSize()) {
        OutlinedTextField(
            value = displayName(selectedOption),
            onValueChange = {}, // Không thay đổi giá trị khi nhập
            readOnly = true, // Chỉ cho phép chọn từ menu
            modifier = Modifier
                .clickable { expanded = true }, // Mở menu khi nhấn
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Mở menu",
                    modifier = Modifier.clickable {
                        expanded = true
                    }
                )
            },
            placeholder = { Text(text = label, color = LadosTheme.colorScheme.onBackground) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
                focusedBorderColor = LadosTheme.colorScheme.primary,
                unfocusedContainerColor = LadosTheme.colorScheme.surfaceContainerHigh,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = LadosTheme.colorScheme.error,
                focusedTextColor = LadosTheme.colorScheme.onBackground,
                unfocusedTextColor = LadosTheme.colorScheme.onBackground,
                errorTextColor = LadosTheme.colorScheme.error,
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = displayName(option)) },
                    onClick = {
                        onOptionSelected(option) // Chọn tùy chọn và cập nhật trạng thái bên ngoài
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun DropdownMenuWithButton(
    options: List<String>,
    label: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }

    Column(modifier = Modifier.wrapContentSize()) {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LadosTheme.colorScheme.surfaceContainerHigh,
                contentColor = LadosTheme.colorScheme.onBackground
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedOption,
                    color = LadosTheme.colorScheme.onBackground,
                    modifier = Modifier
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Mở menu",
                    tint = LadosTheme.colorScheme.primary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onOptionSelected(selectedOption)
                    }
                )
            }
        }
    }
}


