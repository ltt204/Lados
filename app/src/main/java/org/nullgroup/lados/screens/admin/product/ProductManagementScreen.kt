package org.nullgroup.lados.screens.admin.product

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.admin.product.FilterItem
import org.nullgroup.lados.viewmodels.admin.product.ProductManagementScreenViewModel
import org.nullgroup.lados.viewmodels.admin.product.priceOptions
import org.nullgroup.lados.viewmodels.admin.product.ratingOptions
import org.nullgroup.lados.viewmodels.admin.product.sortOptions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: ProductManagementScreenViewModel = hiltViewModel(),
    navController: NavController,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val products by viewModel.editProducts.collectAsState(emptyList())
    val categories by viewModel.categories.collectAsState(emptyList())
    val isLoading by viewModel.isLoading.collectAsState(false)

    LaunchedEffect(Unit) {
            viewModel.loadProducts()
            viewModel.loadCategories()
    }

    var selectedProduct by remember {
        mutableStateOf(null as String?)
    }
    var onDeleteSelected by remember {
        mutableStateOf(false)
    }
    var onUpdateSelected by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()

    Log.d("ManageProductScreen", "products: $products")


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(LadosTheme.colorScheme.background)
            .padding(paddingValues),

        ) { it ->

        var searchQuery by remember { mutableStateOf("") }
        var openFilter by remember { mutableStateOf(false) }
        var openSort by remember { mutableStateOf(false) }
        var openCategory by remember { mutableStateOf(false) }
        var sortOption by remember { mutableStateOf("All") }
        var categoryOption by remember { mutableStateOf("All") }
        var priceFilterOption by remember { mutableStateOf("Default") }
        var ratingFilterOption by remember { mutableStateOf("Default") }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LadosTheme.colorScheme.background)
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchTextField(
                    query = searchQuery,
                    onQueryChange = { value ->
                        searchQuery = value
                        if (value.isEmpty()) {
                            viewModel.searchProducts("")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    onSearch = { viewModel.searchProducts(searchQuery) }
                )

                IconButton(
                    onClick = { openFilter = !openFilter },
                    modifier = Modifier
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_filter),
                        contentDescription = "Filter Button",
                        tint = LadosTheme.colorScheme.onBackground
                    )
                }

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OptionButton(
                    item = FilterItem(title = "Sort: $sortOption"),
                    modifier = Modifier.wrapContentWidth(),
                    onClick = { openSort = true }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {

                        sortOption = "All"
                        categoryOption = "All"
                        priceFilterOption = "Default"
                        ratingFilterOption = "Default"

                        viewModel.sortAndFilter(
                            categoryOption = sortOption,
                            sortOption = categoryOption,
                            priceOption = priceFilterOption,
                            ratingOption = ratingFilterOption
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = LadosTheme.colorScheme.error),
                    modifier = Modifier
                        .weight(1f)

                ) {
                    Text(
                        text = "Reset",
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

            OptionButton(
                item = FilterItem(title = "Category: $categoryOption"),
                modifier = Modifier.wrapContentWidth(),
                onClick = { openCategory = true }
            )

            ManageSection(
                onAddNewProduct = {
                    navController.navigate(Screen.Admin.AddProduct.route)
                },
                onDeleteAllSelected = {}
            )

            if(isLoading){
                Box(
                    modifier = Modifier.fillMaxSize()
                ){
                    CircularProgressIndicator(
                        color = LadosTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products.size) { index ->
                        ProductItem(products[index],
                            onLongClick = {
                                Log.d("ProductItem", "Long Clicked: ${products[index].name}")
                                selectedProduct = products[index].id
                                Log.d("ProductItem", "Product id: ${products[index].id}")
                                Log.d("ProductItem", "Selected Product: $selectedProduct")

                                scope.launch {
                                    sheetState.show()
                                }
                            })
                    }
                }
            }
        }

        FilterDialog(
            isOpen = openFilter,
            onDismiss = { openFilter = false },
            onRatingOptionSelected = {
                ratingFilterOption = it
            },
            onPriceOptionSelected = {
                priceFilterOption = it
            },
            currentPrice = priceFilterOption,
            currentRating = ratingFilterOption,
            onApply = {
                viewModel.sortAndFilter(
                    categoryOption = categoryOption,
                    sortOption = sortOption,
                    priceOption = priceFilterOption,
                    ratingOption = ratingFilterOption
                )
                openFilter = false
            }
        )

        SortDialog(
            isOpen = openSort,
            onDismiss = { openSort = false },
            onSortOptionSelected = {
                sortOption = it
            },
            currentOption = sortOption,
            onApply = {
                viewModel.sortAndFilter(
                    categoryOption = categoryOption,
                    sortOption = sortOption,
                    priceOption = priceFilterOption,
                    ratingOption = ratingFilterOption
                )
                openSort = false
            },
            onReset = {
                openSort = false
            }
        )

        CategoryDialog(
            isOpen = openCategory,
            onDismiss = { openCategory = false },
            options = categories.map { it.categoryName },
            onCategoryOptionSelected = {
                categoryOption = it
            },
            currentOption = categoryOption,
            onApply = {
                viewModel.sortAndFilter(
                    categoryOption = categoryOption,
                    sortOption = sortOption,
                    priceOption = priceFilterOption,
                    ratingOption = ratingFilterOption
                )
                openCategory = false
            }
        )
    }

    if (selectedProduct != null) {
        ModalBottomSheet(
            onDismissRequest = {
                selectedProduct = null
            },
            sheetState = sheetState,
        ) {
            Column {

                TextButton(
                    modifier = Modifier
                        .height(84.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }
                        if (!sheetState.isVisible) {
                            selectedProduct = null
                        }
                        onDeleteSelected = true
                        // TODO: call viewModel to delete product. Care for variant, also image.
                    }) {
                    Text(
                        "Remove product",
                        style = LadosTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = LadosTheme.colorScheme.error
                        )
                    )
                }
                TextButton(
                    modifier = Modifier
                        .height(84.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }
                        if (!sheetState.isVisible) {
                            selectedProduct = null
                        }
                        onUpdateSelected = true

                        navController.navigate(Screen.Admin.EditProduct.route + "/$selectedProduct")

                        // TODO: navigate to  product update. !!Care for variant, also image.
                    }) {
                    Text(
                        "Update product",
                        style = LadosTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = LadosTheme.colorScheme.outline
                        )
                    )
                }
            }

        }
    }
}


@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search",
    modifier: Modifier = Modifier,
    onSearch: () -> Unit = {}
) {

    val focusManager = LocalFocusManager.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = placeholder,
                style = LadosTheme.typography.bodySmall.copy(
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Search"
                    )
                }
            }
        },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = LadosTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(20.dp)
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
            unfocusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            cursorColor = MaterialTheme.colors.primary
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                focusManager.clearFocus()
            }
        ),
        shape = RoundedCornerShape(16.dp),
        textStyle = LadosTheme.typography.bodySmall.copy(
            color = LadosTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun OptionButton(
    item: FilterItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LadosTheme.colorScheme.primary
        ),
        modifier = modifier
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = LadosTheme.typography.bodySmall.copy(
                    color = LadosTheme.colorScheme.onPrimary
                )
            )
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                painter = painterResource(R.drawable.arrowright2),
                contentDescription = "Dropdown Icon",
                modifier = Modifier.rotate(90f),
                tint = LadosTheme.colorScheme.onBackground
            )

        }
    }

}


@Composable
fun ManageSection(
    onAddNewProduct: () -> Unit,
    onDeleteAllSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAddNewProduct,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = "+ New",
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.padding(5.dp)
            )
        }

        Button(
            onClick = onDeleteAllSelected,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = androidx.compose.ui.graphics.Color.Red)
        ) {
            Text(
                text = "Delete All",
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductItem(
    product: Product,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = {}
) {
    val stockCount = product.variants.sumOf { it.quantityInStock }
    val name = product.name
    val salePrice = product.variants.firstOrNull()?.salePrice ?: 0.0
    val originalPrice = product.variants.firstOrNull()?.originalPrice ?: 0.0
    val variantCount = product.variants.size
    val rating = product.engagements.map { it.ratings }.average()
    val commentCount = product.engagements.size
    val image = product.variants.firstOrNull()?.images.orEmpty().firstOrNull()?.link

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = {

                },
                onLongClick = {
                    onLongClick()
                }
            )
            .animateContentSize(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp,
            focusedElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = LadosTheme.colorScheme.surfaceContainer,
        )
    ) {
        Column {
            // Image Container với overlay gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {

                SubcomposeAsyncImage(
                    model = coil.request.ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Loaded Image",
                    loading = {
                        LoadOnProgress(
                            modifier = Modifier,
                            content = { CircularProgressIndicator() }
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Inside
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color.Transparent,
                                    androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                // Stock badge
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(12.dp),
                    color = if (stockCount > 0)
                        LadosTheme.colorScheme.primary.copy(0.9f)
                    else
                        LadosTheme.colorScheme.error.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = if (stockCount > 0) "In Stock: $stockCount" else "Out of Stock",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Product name
                Text(
                    text = name,
                    style = LadosTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = LadosTheme.colorScheme.onBackground
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Price and Rating row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price with currency symbol
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$originalPrice",
                            style = LadosTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LadosTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "$salePrice",
                            style = LadosTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LadosTheme.colorScheme.onPrimary
                            ),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    // Rating
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format("%.2f", rating),
                            style = LadosTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LadosTheme.colorScheme.primary
                            )
                        )

                        Icon(
                            painter = painterResource(R.drawable.star_icon),
                            contentDescription = "Star",
                            modifier = Modifier.size(16.dp),
                            tint = LadosTheme.colorScheme.primary
                        )

                    }
                }

                // Additional info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Variants count
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Style,
                            contentDescription = "Variants",
                            modifier = Modifier.size(16.dp),
                            tint = LadosTheme.colorScheme.onBackground

                        )
                        Text(
                            text = "$variantCount variants",
                            style = LadosTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LadosTheme.colorScheme.onBackground
                            )
                        )
                    }

                    // Comments count
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Comment,
                            contentDescription = "Comments",
                            modifier = Modifier.size(16.dp),
                            tint = LadosTheme.colorScheme.onBackground

                        )
                        Text(
                            text = "$commentCount reviews",
                            style = LadosTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LadosTheme.colorScheme.onBackground
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDialog(
    modifier: Modifier = Modifier,
    isOpen: Boolean = true,
    onRatingOptionSelected: (String) -> Unit = {},
    onPriceOptionSelected: (String) -> Unit = {},
    currentPrice: String = "Default",
    currentRating: String = "Default",
    onDismiss: () -> Unit = {},
    onApply: () -> Unit = {}
) {

    var isReset by remember { mutableStateOf(false) }

    LaunchedEffect(isReset) {
        Log.d("FilterDialog", "isReset: $isReset")
    }

    if (isOpen) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LadosTheme.colorScheme.background
                ),
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()

                ) {
                    DropdownWithTitle(
                        title = "Price",
                        options = priceOptions,
                        isReset = isReset,
                        currentOption = currentPrice,
                        onOptionSelected = {
                            onPriceOptionSelected(it)
                            if (isReset) isReset = false
                        }
                    )
                    DropdownWithTitle(
                        title = "Rating",
                        currentOption = currentRating,
                        options = ratingOptions,
                        isReset = isReset,
                        onOptionSelected = {
                            onRatingOptionSelected(it)
                            if (isReset) isReset = false
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LadosTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = LadosTheme.colorScheme.onError
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = {
                            isReset = true
                            onRatingOptionSelected("Default")
                            onPriceOptionSelected("Default")
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LadosTheme.colorScheme.background
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LadosTheme.colorScheme.primary),

                        ) {
                        Text(
                            text = "Reset",
                            color = LadosTheme.colorScheme.primary
                        )

                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = { onApply() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LadosTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Apply",
                            color = LadosTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SortDialog(
    modifier: Modifier = Modifier,
    isOpen: Boolean = true,
    onDismiss: () -> Unit = {},
    onSortOptionSelected: (String) -> Unit = {},
    onReset: () -> Unit = {},
    currentOption: String = "All",
    onApply: () -> Unit = {}
) {
    var isReset by remember { mutableStateOf(false) }

    if (isOpen) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LadosTheme.colorScheme.background
                ),
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()

                ) {
                    DropdownWithTitle(
                        "Sort by",
                        sortOptions,
                        isReset = isReset,
                        currentOption = currentOption,
                        onOptionSelected = {
                            onSortOptionSelected(it)
                            isReset = false
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LadosTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = LadosTheme.colorScheme.onError
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = {
                            isReset = true
                            onSortOptionSelected("All")
                            //onReset()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LadosTheme.colorScheme.background
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LadosTheme.colorScheme.primary),

                        ) {
                        Text(
                            text = "Reset",
                            color = LadosTheme.colorScheme.primary
                        )

                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = { onApply() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LadosTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Apply",
                            color = LadosTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDialog(
    modifier: Modifier = Modifier,
    isOpen: Boolean = true,
    onDismiss: () -> Unit = {},
    currentOption: String = "All",
    options: List<String> = emptyList(),
    onCategoryOptionSelected: (String) -> Unit = {},
    onReset: () -> Unit = {},
    onApply: () -> Unit = {}
) {


    var isReset by remember { mutableStateOf(false) }

    if (isOpen) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LadosTheme.colorScheme.background
                ),
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()

                ) {
                    DropdownWithTitle(
                        "Category",
                        options,
                        currentOption = currentOption,
                        isReset = isReset,
                        onOptionSelected = {
                            onCategoryOptionSelected(it)
                            isReset = false
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LadosTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = LadosTheme.colorScheme.onError
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = {
                            isReset = true
                            onCategoryOptionSelected("All")
                            //onReset()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LadosTheme.colorScheme.background
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LadosTheme.colorScheme.primary),

                        ) {
                        Text(
                            text = "Reset",
                            color = LadosTheme.colorScheme.primary
                        )

                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = { onApply() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LadosTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Apply",
                            color = LadosTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericDropdown(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = 1.dp,
                color = LadosTheme.colorScheme.outline
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedOption.toString(),
                    style = LadosTheme.typography.bodyLarge,
                    color = LadosTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (expanded) 180f else 0f),
                    tint = LadosTheme.colorScheme.onSurface
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(LadosTheme.colorScheme.background)
                .height(200.dp)
                .verticalScroll(scrollState),
            shadowElevation = MenuDefaults.ShadowElevation,
            shape = RoundedCornerShape(12.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.toString(),
                            style = LadosTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = LadosTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun DropdownWithTitle(
    title: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    currentOption: String,
    isReset: Boolean = false,
    modifier: Modifier = Modifier
) {

    var selectedOption by remember { mutableStateOf(options[0]) }

    if (currentOption.isNotEmpty()) {
        selectedOption = currentOption
    }

    if (isReset) selectedOption = options[0]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp),
            color = LadosTheme.colorScheme.onBackground
        )

        GenericDropdown(
            label = "Rating",
            options = options,
            selectedOption = selectedOption,
            onOptionSelected = {
                selectedOption = it
                onOptionSelected(selectedOption)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
