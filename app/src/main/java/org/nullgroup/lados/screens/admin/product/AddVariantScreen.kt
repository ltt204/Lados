package org.nullgroup.lados.screens.admin.product

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.data.models.AddProductVariant
import org.nullgroup.lados.data.remote.models.ColorRemoteModel
import org.nullgroup.lados.data.remote.models.ProductVariantRemoteModel
import org.nullgroup.lados.data.remote.models.SizeRemoteModel
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.toByteArray
import org.nullgroup.lados.utilities.toDrawable
import org.nullgroup.lados.viewmodels.admin.product.AddProductScreenViewModel
import org.nullgroup.lados.viewmodels.admin.product.VariantImageUiState
import org.nullgroup.lados.viewmodels.admin.product.colorOptionsList
import org.nullgroup.lados.viewmodels.admin.product.exchangePrice
import org.nullgroup.lados.viewmodels.admin.product.sizeOptionsList
import org.nullgroup.lados.viewmodels.admin.product.validatePrice
import org.nullgroup.lados.viewmodels.admin.product.validateQuantity
import org.nullgroup.lados.viewmodels.admin.product.validateSaleAmount
import org.nullgroup.lados.viewmodels.admin.product.validateSalePrice
import org.nullgroup.lados.viewmodels.admin.product.validateVariant


@Composable
fun AddVariantScreen(
    modifier: Modifier = Modifier,
    onVariantAdded: (AddProductVariant) -> Unit = {},
    productId: String? = null,
    viewModel: AddProductScreenViewModel,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val productVariantsState by viewModel.productVariants.collectAsState()
    val uploadImageState by viewModel.uploadImageState.collectAsState()

    var submitVariantClicked by remember { mutableStateOf(false) }

    var size by remember { mutableStateOf(SizeRemoteModel()) }
    var color by remember { mutableStateOf(ColorRemoteModel()) }
    var originalPrice by remember { mutableStateOf("") }
    var salePrice by remember { mutableStateOf("") }
    var priceOption by remember { mutableStateOf("USD") }
    var quantity by remember { mutableStateOf("") }
    var saleAmount by remember { mutableStateOf("") }

    var originalPriceError by remember { mutableStateOf(Pair(true, "")) }
    var salePriceError by remember { mutableStateOf(Pair(true, "")) }
    var quantityError by remember { mutableStateOf(Pair(true, "")) }
    var saleAmountError by remember { mutableStateOf(Pair(true, "")) }
    var variantError by remember { mutableStateOf(Pair(true, "")) }

    var originalPriceFocus by remember { mutableStateOf(false) }
    var salePriceFocus by remember { mutableStateOf(false) }
    var quantityFocus by remember { mutableStateOf(false) }
    var saleAmountFocus by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageByteArray by remember { mutableStateOf<ByteArray?>(null) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->

        uri?.toDrawable(context)?.toByteArray()?.let {
            imageByteArray = it
        }

        uri?.let {
            selectedImageUri = it
        }
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding()),
        containerColor = LadosTheme.colorScheme.background,
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
                        variantError = validateVariant(
                            color.colorName["en"] ?: "",
                            size.sizeName["en"] ?: "",
                            productVariantsState
                        )

                        quantityError = validateQuantity(quantity)
                        originalPriceError = validatePrice(originalPrice, priceOption)
                        salePriceError = validateSalePrice(salePrice, priceOption, originalPrice)
                        saleAmountError = validateSaleAmount(saleAmount, quantity)

                        if (
                            variantError.first &&
                            salePriceError.first &&
                            originalPriceError.first &&
                            quantityError.first &&
                            saleAmountError.first
                        ) {

                            val variant = ProductVariantRemoteModel(
                                productId = productId ?: "",
                                color = color,
                                size = size,
                                quantityInStock = quantity.toInt(),
                                originalPrice = exchangePrice(originalPrice, priceOption),
                                salePrice = exchangePrice(salePrice, priceOption),
                                saleAmount = 0,
                                images = listOf()
                            )

                            viewModel.onAddVariant(
                                variant = variant,
                                withImageByteArray = imageByteArray ?: ByteArray(0)
                            )

                            submitVariantClicked = true
                        }


                    }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),

            ) {

            VariantImageSection(
                imageUri = selectedImageUri?.toString()
                    ?: "https://firebasestorage.googleapis.com/v0/b/lados-8509b.firebasestorage.app/o/images%2Fproducts%2Fimg_placeholder.jpg?alt=media&token=1f1fed12-8ead-4433-b2a4-c5e1d765290e",
                onEditImage = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
            if (!variantError.first) {
                Text(
                    text = variantError.second,
                    style = LadosTheme.typography.bodySmall,
                    color = LadosTheme.colorScheme.error
                )
            }

            // Row for color and size dropdown menus
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(), horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = "Color",
                        style = LadosTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = LadosTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenuWithTextField(
                        options = colorOptionsList,
                        label = "Choose color",
                        onOptionSelected = {
                            color = it
                        },
                        displayName = { it.colorName["en"] ?: "" },
                        selectedOption = color
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Size",
                        style = LadosTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = LadosTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenuWithTextField(
                        options = sizeOptionsList,
                        label = "Choose size",
                        onOptionSelected = {
                            size = it
                        },
                        displayName = { it.sizeName["en"] ?: "" },
                        selectedOption = size
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quantity",
                        style = LadosTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = LadosTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                    CustomTextField(label = "Quantity",
                        text = quantity,
                        onValueChange = {
                            if (it.matches(Regex("^[0-9]*$"))) {
                                quantity = it
                                quantityError = validateQuantity(quantity)
                            }

                            quantityFocus = true

                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                quantityError = validateQuantity(quantity)
                            }
                        ),
                        isError = !quantityError.first,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (!quantityFocus) return@onFocusChanged
                                if (!it.isFocused) quantityError = validateQuantity(quantity)
                            })
                    if (!quantityError.first) {
                        Text(
                            text = quantityError.second,
                            style = LadosTheme.typography.bodySmall,
                            color = LadosTheme.colorScheme.error
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Price (USD only)",
                        style = LadosTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = LadosTheme.colorScheme.onBackground,
                    )
                    CustomTextField(label = "Enter price",
                        text = originalPrice,
                        onValueChange = {
                            originalPrice = it
                            originalPriceFocus = true
                            originalPriceError = validatePrice(originalPrice, priceOption)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                originalPriceError = validatePrice(originalPrice, priceOption)
                            }
                        ),
                        isError = !originalPriceError.first,
                        modifier = Modifier
                            .onFocusChanged {
                                if (!originalPriceFocus) return@onFocusChanged
                                if (!it.isFocused) originalPriceError =
                                    validatePrice(originalPrice, priceOption)
                            })

                    if (!originalPriceError.first) {
                        Text(
                            text = originalPriceError.second,
                            style = LadosTheme.typography.bodySmall,
                            color = LadosTheme.colorScheme.error
                        )
                    }
                }
            }

            Text(
                text = "Sale",
                style = LadosTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = LadosTheme.colorScheme.onBackground,
            )

            CustomTextField(
                label = "Enter sale price",
                text = salePrice,
                onValueChange = {
                    salePrice = it
                    salePriceFocus = true
                    salePriceError = validateSalePrice(salePrice, priceOption, originalPrice)
                },
                isError = !salePriceError.first,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        salePriceError = validateSalePrice(salePrice, priceOption, originalPrice)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (!salePriceFocus) return@onFocusChanged
                        if (!it.isFocused) salePriceError =
                            validateSalePrice(salePrice, priceOption, originalPrice)
                    }
            )

            if (!salePriceError.first) {
                Text(
                    text = salePriceError.second,
                    style = LadosTheme.typography.bodySmall,
                    color = LadosTheme.colorScheme.error
                )
            }

            //           Spacer(modifier = Modifier.width(16.dp))
//
//            Text(
//                text = "Sale amount",
//                style = LadosTheme.typography.titleSmall,
//                fontWeight = FontWeight.Bold,
//                color = LadosTheme.colorScheme.onBackground,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            CustomTextField(label = "Sale amount",
//                text = saleAmount,
//                isError = !saleAmountError.first,
//                onValueChange = {
//                    if (it.matches(Regex("^[0-9]*$"))) {
//                        saleAmount = it
//                    }
//                    saleAmountError = validateSaleAmount(saleAmount, quantity)
//                    saleAmountFocus = true
//                },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
//                ),
//                keyboardActions = KeyboardActions(
//                    onDone = {
//                        focusManager.clearFocus()
//                        saleAmountError = validateSaleAmount(saleAmount, quantity)
//                    }
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .onFocusChanged {
//                        if (!saleAmountFocus) return@onFocusChanged
//                        if (!it.isFocused) saleAmountError =
//                            validateSaleAmount(saleAmount, quantity)
//                    })
//
//            if (!saleAmountError.first) {
//                Text(
//                    text = saleAmountError.second,
//                    style = LadosTheme.typography.bodySmall,
//                    color = LadosTheme.colorScheme.error
//                )
//            }
//
//            Spacer(modifier = Modifier.width(8.dp))

        }
    }

    if (submitVariantClicked) {
        Log.d("AddVariantScreen", "Submit variant clicked")
        when (uploadImageState) {
            is VariantImageUiState.Initial -> {
                Log.d("AddVariantScreen", "Initial")
                // Initial
            }

            is VariantImageUiState.Loading -> {
                LoadOnProgress {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                }
            }

            is VariantImageUiState.Success -> {
                Log.d("AddVariantScreen", "Variant added")
                submitVariantClicked = false
                navController.navigateUp()
            }

            is VariantImageUiState.Error -> {
                // Error
            }

            else -> {}
        }
    }
}

@Composable
fun VariantImageSection(
    modifier: Modifier = Modifier,
    imageUri: String,
    onEditImage: () -> Unit = {},
) {

    Box(
        modifier = modifier
            .wrapContentSize()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                loading = {
                    LoadOnProgress(
                        modifier = Modifier
                            .clip(CircleShape)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                    }
                },
                model = ImageRequest
                    .Builder(context = LocalContext.current)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Variant Image"
            )
        }

        OutlinedIconButton(
            modifier = Modifier
                .clip(CircleShape)
                .align(Alignment.BottomEnd)
                .size(30.dp),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                contentColor = Color.White,
                containerColor = Color(0xFF8E6CEF)
            ),
            border = BorderStroke(2.dp, Color.White),
            onClick = onEditImage
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Filled.Edit,
                contentDescription = null
            )
        }
    }
}



