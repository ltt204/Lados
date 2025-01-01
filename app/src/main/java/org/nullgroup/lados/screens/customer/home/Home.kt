package org.nullgroup.lados.screens.customer.home

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.product.FilterCategory
import org.nullgroup.lados.screens.customer.product.FilterState
import org.nullgroup.lados.screens.customer.product.PriceSlider
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.Primary
import org.nullgroup.lados.utilities.toCurrency
import org.nullgroup.lados.viewmodels.customer.home.CategoryUiState
import org.nullgroup.lados.viewmodels.customer.home.HomeViewModel
import org.nullgroup.lados.viewmodels.customer.home.ProductUiState
import org.nullgroup.lados.viewmodels.SharedViewModel
import java.text.DecimalFormat

@Composable
fun SearchBarRow(
    modifier: Modifier = Modifier,
    navController: NavController,
    direct: Boolean = false,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .height(56.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        SearchBar(
            navController = navController,
            onSearch = {},
            direct = direct,
        )
    }

}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    onSearch: (String) -> Unit,
    direct: Boolean = false,
) {
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { if (direct) navController.navigate(Screen.Customer.SearchScreen.route) },

        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { if (!direct) searchText = it },
            enabled = !direct,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            singleLine = true,
            placeholder = {
                Text(
                    stringResource(R.string.search),
                    style = LadosTheme.typography.bodyLarge.copy(
                        color = LadosTheme.colorScheme.onBackground
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(30.dp),
                    tint = LadosTheme.colorScheme.onBackground,
                )
            },
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
                focusedBorderColor = LadosTheme.colorScheme.primary,
                unfocusedContainerColor = LadosTheme.colorScheme.surfaceContainerHigh,
                unfocusedBorderColor = if (direct) Primary else LadosTheme.colorScheme.onBackground,
                errorBorderColor = LadosTheme.colorScheme.error,
                focusedTextColor = LadosTheme.colorScheme.onBackground,
                unfocusedTextColor = LadosTheme.colorScheme.onBackground,
                errorTextColor = LadosTheme.colorScheme.error,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch(searchText)
                focusManager.clearFocus()
            })
        )
    }
}

@Composable
fun CategoryCircle(
    modifier: Modifier = Modifier,
    imageUrl: String,
) {
    Box(
        modifier
            .clip(CircleShape)
            // note: modify
            .background(LadosTheme.colorScheme.surfaceContainerHighest)
            .padding(0.dp)
            .size(64.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = modifier
                .padding(12.dp),
            colorFilter = ColorFilter.tint(
                LadosTheme.colorScheme.primary,
            )
        )
    }
}

@Composable
fun TitleTextRow(
    modifier: Modifier = Modifier,
    contentLeft: String,
    contentRight: String,
    // note: modify
    color: Color = LadosTheme.colorScheme.tertiary,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = contentLeft,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        )

        TextButton(
            onClick = onClick
        ) {
            Text(
                contentRight, style = TextStyle(
                    fontSize = 18.sp,
                    // note: modify
                    color = LadosTheme.colorScheme.onBackground,
                )
            )
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = LadosTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
fun CategoryItems(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    navController: NavController,
) {
    val categoryUiState = viewModel.categoryUiState.collectAsStateWithLifecycle()
    when (categoryUiState.value) {
        is CategoryUiState.Loading -> {
            LoadOnProgress(
                modifier = modifier.fillMaxHeight(),
                content = {
                    CircularProgressIndicator()
                }
            )
        }

        is CategoryUiState.Error -> {
            Text(text = "Failed to load data")
        }

        is CategoryUiState.Success -> {
            val categories = (categoryUiState.value as CategoryUiState.Success).categories
            LazyRow(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(categories)
                { category ->
                    CategoryItem(
                        category = category,
                        modifier = Modifier.clickable {
                            sharedViewModel.updateTypeScreen("In Category")
                            sharedViewModel.updateComplexData(category)
                            navController.navigate(Screen.Customer.DisplayProductInCategory.route)
                        })
                }
            }
        }

        else -> {}
    }


}

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    category: Category,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        CategoryCircle(imageUrl = category.categoryImage)
        Spacer(Modifier.height(8.dp))
        // note: modify color
        Text(
            text = category.categoryName,
            color = LadosTheme.colorScheme.onBackground,
            fontSize = 16.sp
        )
    }
}


@Composable
fun ProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: (String) -> Unit,
    onFavicon: (String) -> Unit = {}
) {
    var isClicked by remember { mutableStateOf(false) }

    Column(modifier = modifier
        .wrapContentHeight()
        .widthIn(max = 160.dp)
        .heightIn(min = 320.dp)
        .clip(RoundedCornerShape(8.dp))
        // note: modify
        .background(LadosTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f))
        .clickable { onClick(product.id) }
    ) {
        Box(modifier = Modifier.weight(1f)) {
            AsyncImage(
                model = product.variants.first().images.first().link,
                contentDescription = "Image",
                modifier = modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop,
            )
            Image(
                painter = painterResource(
                    if (!isClicked) R.drawable.love
                    else R.drawable.heart
                ),
                contentDescription = "Image",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.8f),
                        CircleShape
                    )
                    .padding(4.dp)
                    .clickable {
                        isClicked = !isClicked
                        onFavicon(product.id)
                    }
            )
        }
        Spacer(Modifier.height(4.dp))
        Column(
            modifier = Modifier
                .weight(0.5f)
                .wrapContentHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                modifier = Modifier.weight(0.5f),
                text = product.name,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 16.sp,
                    // note: modify
                    color = LadosTheme.colorScheme.onBackground,
                )
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                val isSale = product.variants.first().salePrice != null
                Text(
                    text = product.variants.first().originalPrice.toCurrency(),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = LadosTheme.colorScheme.onSurface.copy(
                            alpha = if (isSale) 0.5f else 1.0f,
                        ),
                        textDecoration = if (isSale) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                if (isSale) {
                    Text(
                        text = product.variants.first().salePrice!!.toCurrency(),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            // note: modify
                            color = LadosTheme.colorScheme.onBackground,
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.weight(0.5f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = LadosTheme.colorScheme.yellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    val decimalFormat = DecimalFormat("#.##")
                    Text(
                        text = decimalFormat.format(product.engagements.sumOf { it.ratings } * 1.0f / product.engagements.size),
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = LadosTheme.colorScheme.onBackground,
                        )
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${product.engagements.size})",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = LadosTheme.colorScheme.onBackground,
                    )
                )
            }
        }
    }
}


@Composable
fun ProductRow(
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    products: List<Product> = emptyList(),
) {
    LazyRow(
        modifier = modifier.heightIn(min = 280.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items = products, key = { it.id })
        { item ->
            ProductItem(
                product = item,
                onClick = onProductClick
            )
        }
    }
}

fun Product.hasNoSalePrice(): Boolean {
    return variants.none { it.salePrice != null}
}

fun Product.isProductOnSale(): Triple<Boolean, Double?, Double?> {
    val saleVariant = variants.find { it.salePrice != null}
    return if (saleVariant != null) {
        Triple(true, saleVariant.salePrice, saleVariant.originalPrice)
    } else {
        Triple(false, null, null)
    }
}

fun Product.sumOfSaleAmount(): Int {
    return variants.sumOf { it.saleAmount }
}


@Composable
fun DrawProductScreenContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    onProductClick: (String) -> Unit,
    viewModel: HomeViewModel,
) {
    val productUiState = viewModel.productUiState.collectAsStateWithLifecycle()
    when (productUiState.value) {
        is ProductUiState.Loading -> {
            LoadOnProgress(
                modifier = modifier,
                content = { CircularProgressIndicator() }
            )
        }

        is ProductUiState.Error -> {
            Text(
                text = "Failed to load data",
                style = LadosTheme.typography.headlineSmall.copy(
                    color = LadosTheme.colorScheme.error,
                )
            )
        }

        is ProductUiState.Success -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = paddingValues.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                item {
                    SearchBarRow(
                        navController = navController,
                        direct = true,
                        modifier = modifier,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                item {
                    TitleTextRow(
                        contentLeft = stringResource(R.string.home_categories_header),
                        contentRight = stringResource(R.string.home_see_all),
                        onClick = {
                            sharedViewModel.updateTypeScreen("In Category")
                            navController.navigate(
                                Screen.Customer.CategorySelectScreen.route
                            )
                        }
                    )
                }

                item {
                    CategoryItems(
                        sharedViewModel = sharedViewModel,
                        navController = navController
                    )
                }

                item {
                    TitleTextRow(
                        contentLeft = stringResource(R.string.on_sale),
                        contentRight = stringResource(R.string.home_see_all),
                        onClick = {
                            sharedViewModel.updateTypeScreen("On Sale")
                            navController.navigate(
                                Screen.Customer.DisplayProductInCategory.route
                            )
                        }
                    )
                }
                item {
                    ProductRow(
                        onProductClick = onProductClick,
                        products = (productUiState.value as ProductUiState.Success).products.filter{ !it.hasNoSalePrice() }
                            .take(5)
                    )
                }

                item {
                    TitleTextRow(
                        contentLeft = stringResource(R.string.home_top_selling),
                        contentRight = stringResource(R.string.home_see_all),
                        onClick = {
                            sharedViewModel.updateTypeScreen("Top Selling")
                            navController.navigate(
                                Screen.Customer.DisplayProductInCategory.route
                            )
                        }
                    )
                }

                item {
                    ProductRow(
                        onProductClick = onProductClick,
                        products = (productUiState.value as ProductUiState.Success).products.sortedByDescending { it.sumOfSaleAmount() }
                            .take(5)
                    )
                }

                item {
                    TitleTextRow(
                        contentLeft = stringResource(R.string.home_new_in),
                        contentRight = stringResource(R.string.home_see_all),
                        onClick = {
                            sharedViewModel.updateTypeScreen("New In")
                            navController.navigate(
                                Screen.Customer.DisplayProductInCategory.route
                            )
                        }
                    )
                }
                item {
                    ProductRow(
                        onProductClick = onProductClick,
                        products = (productUiState.value as ProductUiState.Success).products.sortedByDescending { it.createdAt }
                            .take(1)
                    )
                }

                item {
                    TitleTextRow(
                        contentLeft = stringResource(R.string.all_products),
                        contentRight = stringResource(R.string.home_see_all),
                        onClick = {
                            sharedViewModel.updateTypeScreen("All Products")
                            navController.navigate(
                                Screen.Customer.DisplayProductInCategory.route
                            )
                        }
                    )
                }
                item {
                    ProductRow(
                        onProductClick = onProductClick,
                        products = (productUiState.value as ProductUiState.Success).products.take(5)
                    )
                }
            }
        }
    }
}

private fun getFilterCategoryAndIndex(title: String, index: Int? = null): Pair<FilterCategory, Int?> {
    return when (title) {
        "Category" -> FilterCategory.CATEGORIES to index
        "Sort by" -> FilterCategory.SORT_BY to index
        "Rating" -> FilterCategory.RATING_RANGE to index
        "Pricing Range" -> FilterCategory.PRICING_RANGE to null // or appropriate index if needed
        else -> FilterCategory.PRICE to index
    }
}

@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    title: String,
    options: List<String>,
    onSelectionChanged: (String) -> Unit = {},
    paddingValues: PaddingValues,
    onCloseClick: () -> Unit,
    onClearClick: (String) -> Unit = {},
    onSliderChanged: (ClosedFloatingPointRange<Float>) -> Unit = {},
    selectedFilters: MutableMap<FilterCategory, Int?> = mutableMapOf(),
) {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.5f)
            // note: modify
            .background(LadosTheme.colorScheme.background)
            .clip(RoundedCornerShape(16.dp))
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 8.dp,
            )
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = {
                        val (category, _) = getFilterCategoryAndIndex(title)
                        selectedFilters[category] = null
                        onClearClick(title)
                        onCloseClick()
                    }
                ) {
                    Text(
                        "Clear", style = LadosTheme.typography.titleMedium.copy(
                            color = LadosTheme.colorScheme.primary,
                        )
                    )
                }
                Text(
                    text = title, style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = LadosTheme.colorScheme.onBackground
                    )
                )
                IconButton(onClick = onCloseClick) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = LadosTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (title == "Pricing Range") {
                val minPrice = 0f
                val maxPrice = 200f
                var currentPriceRange by remember { mutableStateOf(minPrice..maxPrice) }
                PriceSlider(
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    currentPriceRange = currentPriceRange,
                    onPriceRangeChanged = { newRange ->
                        currentPriceRange = newRange
                        onSliderChanged(newRange)
                    }
                )
            } else
                options.forEachIndexed { index, option ->
                    val (category, index) = getFilterCategoryAndIndex(title, index)
                    val isSelected = selectedFilters[category] == index
                    Button(

                        onClick = {
                            val (category, index) = getFilterCategoryAndIndex(title, index)
                            selectedFilters[category] = index

                            onSelectionChanged(option)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(

                            if (isSelected
                            )
                                LadosTheme.colorScheme.primary else
                                LadosTheme.colorScheme.surfaceContainerHighest
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.95f),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected
                                )

                                    Color.White else
                                    LadosTheme.colorScheme.onBackground
                            )
                            if (isSelected
                            )

                                Icon(
                                    Icons.Outlined.Done,
                                    contentDescription = null,
                                    tint = if (isSelected
                                    )

                                        LadosTheme.colorScheme.primary else
                                        LadosTheme.colorScheme.surfaceContainerHighest
                                )
                        }
                    }
                }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    sharedViewModel: SharedViewModel = SharedViewModel(),
    viewModel: HomeViewModel = hiltViewModel(),
) {
    Scaffold(
        modifier = modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .padding(horizontal = 16.dp),
        containerColor = LadosTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LadosTheme.colorScheme.background
                ),
                title = {
                    Text(
                        text = "Lados",
                        style = LadosTheme.typography.headlineSmall.copy(
                            color = LadosTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Screen.Customer.CartScreen.route) },
                        modifier = Modifier
                            .clip(LadosTheme.shape.full)
                            .background(LadosTheme.colorScheme.primary),
                    ) {
                        Icon(
                            Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = LadosTheme.colorScheme.surfaceContainerHighest,
                        )
                    }
                }
            )
        }
    ) { it ->
        DrawProductScreenContent(
            modifier = modifier,
            paddingValues = it,
            navController = navController,
            sharedViewModel = sharedViewModel,
            onProductClick = { id ->
                navController.navigate(Screen.Customer.ProductDetailScreen.route + "/$id")
            },
            viewModel = viewModel
        )
    }
}


@Preview(
    name = "Summary",
    showBackground = true,
    showSystemUi = true
)

@Composable
fun Summary() {
    LadosTheme {
        ProductScreen(
            navController = NavController(LocalContext.current)
        )
    }
}

