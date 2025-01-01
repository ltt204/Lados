package org.nullgroup.lados.screens.customer.product

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.data.local.SearchHistoryManager
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.home.BottomSheetContent
import org.nullgroup.lados.screens.customer.home.DrawError_FindNotMatch
import org.nullgroup.lados.screens.customer.home.ProductItem
import org.nullgroup.lados.screens.customer.home.SearchBar
import org.nullgroup.lados.screens.customer.home.hasNoSalePrice
import org.nullgroup.lados.screens.customer.home.sumOfSaleAmount
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.SharedViewModel
import org.nullgroup.lados.viewmodels.customer.home.CategoryUiState
import org.nullgroup.lados.viewmodels.customer.home.HomeViewModel
import org.nullgroup.lados.viewmodels.customer.home.ProductUiState


@Composable
fun FilterButton(
    text: String,
    type: String,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onButtonClick: (String) -> Unit = {},
    isNormal: Boolean=true,
    isSelected: Boolean=false,
    isVisible: Boolean=true
) {
    //var selectedButton by remember { mutableStateOf(isSelected) }
    Button(
        onClick = {
            onButtonClick(type)
            //if (isNormal) selectedButton=!selectedButton
        },
        enabled = isVisible,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            if (isSelected) LadosTheme.colorScheme.onPrimaryContainer else LadosTheme.colorScheme.primaryContainer
        ),

        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(text = text, color = if (isSelected) LadosTheme.colorScheme.primaryContainer else LadosTheme.colorScheme.onPrimaryContainer)
            if (icon != null) {
                Icon(
                    icon,
                    tint = if (isSelected) LadosTheme.colorScheme.primaryContainer else LadosTheme.colorScheme.onPrimaryContainer,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun ProductsGrid(
    modifier: Modifier = Modifier,
    navController: NavController,
    products: List<Product> = emptyList(),
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(products.size) { item ->
            ProductItem(
                product = products[item],
                onClick = { id ->
                    navController.navigate(
                        Screen.Customer.ProductDetailScreen.route + "/$id"
                    )
                }
            )
        }
    }
}

@Composable
fun DrawProductInCategoryScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    textStyle: TextStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LadosTheme.colorScheme.onBackground),
    paddingValues: PaddingValues,
    products: List<Product> = emptyList(),
    sharedViewModel: SharedViewModel = SharedViewModel(),
    onButtonClick: (String) -> Unit ={},
    onNormalButtonClick: (String) -> Unit={},
    isSelected: MutableMap<FilterCategory, Boolean>,
    inCategory: Boolean = false,
    inNewest: Boolean=false,
    inTopSelling: Boolean=false,
    inSearch: Boolean=false,
    inOnSale: Boolean=false,
    inAllProducts: Boolean=false,
) {
    if (inSearch && products.none { it.name.contains(sharedViewModel.searchQuery ?: "") }) {
        DrawError_FindNotMatch(
            modifier = modifier
                .padding(horizontal = 8.dp),
            navController = navController,
            paddingValues = paddingValues
        )
    }
    else {
        Column(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = modifier
            ) {
                items(6) { index ->
                    when (index) {
                        0 -> if (!inCategory) FilterButton(
                            text = stringResource(R.string.category),
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Category Filter",
                            type = stringResource(R.string.category),
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[FilterCategory.CATEGORIES]==true,
                        )

                        1 -> if (!inOnSale) FilterButton(
                            text = stringResource(R.string.on_sale),
                            contentDescription = "On Sale Filter",
                            type = stringResource(R.string.on_sale),
                            onButtonClick = onNormalButtonClick,
                            isSelected = isSelected[FilterCategory.ON_SALE]==true,
                        )

                        2 -> FilterButton(
                            text = stringResource(R.string.price),
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Price Sort",
                            type = stringResource(R.string.price),
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[FilterCategory.PRICE]==true,
                        )

                        3 -> FilterButton(
                            text = stringResource(R.string.sort_by),
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Sort by",
                            type = stringResource(R.string.sort_by),
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[FilterCategory.SORT_BY]==true,
                        )

                        4 -> FilterButton(
                            text = stringResource(R.string.rating),
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Rating Filter",
                            type = stringResource(R.string.rating),
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[FilterCategory.RATING_RANGE]==true,
                        )

                        5-> FilterButton(
                            text= stringResource(R.string.pricing_range),
                            icon = Icons.Outlined.KeyboardArrowDown,
                            type = stringResource(R.string.pricing_range),
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[FilterCategory.PRICING_RANGE]==true,
                        )
                    }
                }
            }
            sharedViewModel.sharedData?.let {
                Title(
                    content = it.categoryName,
                    textStyle = textStyle
                )
            }
            ProductsGrid(
                navController = navController,
                products = if (inTopSelling)
                    products.sortedBy { it.sumOfSaleAmount() }
                else if (inNewest)
                    products.sortedByDescending { it.createdAt }
                else if (inCategory)
                    products.filter { it.categoryId == sharedViewModel.sharedData?.categoryId }
                else if (inSearch)
                    products.filter { it.name.contains(sharedViewModel.searchQuery ?: "") }
                else if (inOnSale)
                    products.filter { !it.hasNoSalePrice() }
                else
                    products
            )
        }
    }
}

@Composable
fun PriceSlider(
    minPrice: Float,
    maxPrice: Float,
    currentPriceRange: ClosedFloatingPointRange<Float>,
    onPriceRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(currentPriceRange) }

    Column {
        RangeSlider(
            value = sliderPosition,
            onValueChange = { newRange ->
                sliderPosition = newRange
                onPriceRangeChanged(newRange)
            },
            valueRange = minPrice..maxPrice,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${sliderPosition.start.toInt()} ", 
                modifier = Modifier.weight(1f),
                color = LadosTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = " ${sliderPosition.endInclusive.toInt()}",
                modifier = Modifier.weight(1f),
                color = LadosTheme.colorScheme.onBackground
            )
        }
    }
}

fun Product.getAverageRating(): Float {
    Log.d("Product", "getAverageRating: ${engagements.sumOf { it.ratings } * 1.0f / engagements.size}")
    return engagements.sumOf { it.ratings } * 1.0f / engagements.size
}

data class FilterState(
    var selectedCategories: String?=null,
    var isOnSale: Boolean = false,
    var price: String? = null,
    var sortBy: String?=null,
    var ratingRange: ClosedFloatingPointRange<Float>?=null,
    var pricingRange: ClosedFloatingPointRange<Float>? = null // Assuming this is different from priceRange
)

enum class FilterCategory {
    CATEGORIES,
    ON_SALE,
    PRICE,
    SORT_BY,
    RATING_RANGE,
    PRICING_RANGE
}

fun updateSelected(selectedFilters: MutableMap<FilterCategory, Boolean>, filterState: FilterState) {
    selectedFilters[FilterCategory.CATEGORIES] = filterState.selectedCategories != null
    selectedFilters[FilterCategory.ON_SALE] = filterState.isOnSale
    selectedFilters[FilterCategory.PRICE] = filterState.price != null
    selectedFilters[FilterCategory.SORT_BY] = filterState.sortBy != null
    selectedFilters[FilterCategory.RATING_RANGE] = filterState.ratingRange != null
    selectedFilters[FilterCategory.PRICING_RANGE] = filterState.pricingRange != null
}

@Composable
fun ProductInCategoryScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
    sharedViewModel: SharedViewModel = SharedViewModel(),
    viewModel: HomeViewModel = hiltViewModel(),
    context: Context
) {
    val productUiState = viewModel.productUiState.collectAsStateWithLifecycle().value
    val typeScreen = sharedViewModel.typeScreen
    val filterState by remember { mutableStateOf(FilterState()) }
    val selectedFilters = remember { mutableStateMapOf<FilterCategory, Boolean>() }
    updateSelected(selectedFilters, filterState)

    val selectedOptions = remember { mutableStateMapOf<FilterCategory, Int?>() }

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val searchHistoryManager = remember { SearchHistoryManager(context) }

    val scope = rememberCoroutineScope()
    var sheetContent by remember {
        mutableStateOf<@Composable () -> Unit>({})
    }

    val optionsMap = mapOf(
        "Sort by" to if (typeScreen!="Newest")listOf(
            "Recommended",
            "Newest",
        ) else listOf("Recommended"),
        "Price" to listOf("Lowest - Highest Price", "Highest - Lowest Price"),
        "Category" to listOf("Pant", "Crop-top", "Top"),
        "Rating" to listOf("1.0 - 2.0", "2.0 - 3.0", "3.0 - 4.0", "4.0 +"),

        "Sắp xếp" to if (typeScreen!="Newest")listOf(
            "Đề xuất",
            "Mới nhất",
        ) else listOf("Đề xuất"),
        "Giá" to listOf("Giá từ thấp đến cao", "Giá từ cao đến thấp"),
        "Loại" to listOf("Quần", "Crop-top", "Thân trên"),
        "Đánh giá" to listOf("1.0 - 2.0", "2.0 - 3.0", "3.0 - 4.0", "4.0 +")
    )
    ModalBottomSheetLayout(
        sheetState = sheetState,
        modifier = Modifier.padding(
            bottom = paddingValues.calculateBottomPadding()
        ),
        sheetBackgroundColor = LadosTheme.colorScheme.background,
        sheetContentColor = LadosTheme.colorScheme.onBackground,
        sheetShape = RoundedCornerShape(
            topStart = 30.dp,
            topEnd = 30.dp
        ),
        sheetContent = { sheetContent() }
    ) {
        Scaffold(
            modifier = modifier
               // .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp),
            containerColor = LadosTheme.colorScheme.background,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .clip(CircleShape)
                            // note: modify
                            .background(LadosTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Search",
                            // note: modify
                            tint = LadosTheme.colorScheme.outline

                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    SearchBar(
                        modifier = Modifier.fillMaxWidth(),
                        navController = navController,
                        onSearch = { query ->
                            (context as ComponentActivity).lifecycleScope.launch {
                                searchHistoryManager.addSearchQuery(query)
                            }
                            sharedViewModel.updateSearchQuery(query)
                            sharedViewModel.updateTypeScreen("In Search")
                            navController.navigate(
                                Screen.Customer.DisplayProductInCategory.route
                            )
                        }
                    )
                }
            }
        ) { it ->
            when (productUiState) {
                is ProductUiState.Loading -> {
                    LoadOnProgress(
                        modifier = modifier.fillMaxHeight(),
                        content = {
                            CircularProgressIndicator()
                        }
                    )
                }

                is ProductUiState.Error -> {
                    // Show error message
                }

                is ProductUiState.Success -> {
                    DrawProductInCategoryScreenContent(
                        modifier = modifier,
                        paddingValues = it,
                        navController = navController,
                        products = productUiState.products,
                        sharedViewModel = sharedViewModel,
                        inCategory = typeScreen == "In Category",
                        inTopSelling = typeScreen == "Top Selling",
                        inNewest = typeScreen == "Newest",
                        inSearch = typeScreen == "In Search",
                        inOnSale = typeScreen == "On Sale",
                        inAllProducts = typeScreen == "All Products",
                        onNormalButtonClick = { _ ->
                            if (selectedFilters[FilterCategory.ON_SALE] == true) {
                                filterState.isOnSale = false
                                updateSelected(selectedFilters, filterState)
                                viewModel.filterProducts(filterState)
                            } else {
                                filterState.isOnSale = true
                                updateSelected(selectedFilters, filterState)
                                viewModel.filterProducts(filterState)
                            }
                        },
                        onButtonClick = { content ->
                            Log.d("FilterButton", "onButtonClick: $content")
                            if (content==context.getString(R.string.pricing_range)) {
                                sheetContent = {
                                    BottomSheetContent(
                                        title = content,
                                        paddingValues = paddingValues,
                                        onClearClick = {
                                            filterState.pricingRange=null
                                            updateSelected(selectedFilters, filterState)
                                            viewModel.filterProducts(filterState)
                                        },
                                        onSliderChanged = { sliderRange ->
                                            filterState.pricingRange=sliderRange
                                            updateSelected(selectedFilters, filterState)
                                            Log.d("BSC", "onSliderChanged: $filterState")
                                            viewModel.filterProducts(filterState)
                                        },
                                        onCloseClick = {
                                            scope.launch {
                                                sheetState.hide()
                                            }
                                        },
                                        options = emptyList(),
                                        selectedFilters = selectedOptions,
                                        context = context
                                    )
                                }
                                scope.launch { sheetState.show() }
                            }
                            else {
                                val option = optionsMap[content] ?: emptyList()
                                if (option.isNotEmpty()) {
                                    sheetContent = {
                                        BottomSheetContent(
                                            selectedFilters = selectedOptions,
                                            title = content,
                                            options = option,
                                            paddingValues = paddingValues,
                                            onClearClick = { clearOption ->
                                                when (clearOption) {
                                                    context.getString(R.string.sort_by) -> {
                                                        filterState.sortBy=null
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    context.getString(R.string.price) -> {
                                                        filterState.price=null
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    context.getString(R.string.category) -> {
                                                        filterState.selectedCategories=null
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    context.getString(R.string.rating) -> {
                                                        filterState.ratingRange=null
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }
                                                }
                                            },
                                            onSelectionChanged = { selectedOption ->
                                                Log.d("BottomSheetContent", "onSelectionChanged: $selectedOption")
                                                when (selectedOption) {
                                                    context.getString(R.string.lowest_highest_price) -> {
                                                        filterState.price="Price (Low to High)"
                                                        filterState.sortBy=null
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    context.getString(R.string.highest_lowest_price) -> {
                                                        filterState.price="Price (High to Low)"
                                                        filterState.sortBy=null
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    context.getString(R.string.recommended) -> {
                                                        filterState.price=null
                                                        filterState.sortBy="Recommended"
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    context.getString(R.string.newest) -> {
                                                        filterState.price=null
                                                        filterState.sortBy="Newest"
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    context.getString(R.string.pant) -> {
                                                        filterState.selectedCategories=context.getString(R.string.pant)
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    context.getString(R.string.crop_top) -> {
                                                        filterState.selectedCategories=context.getString(R.string.crop_top) 
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    context.getString(R.string.top) -> {
                                                        filterState.selectedCategories=context.getString(R.string.top)  
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    "1.0 - 2.0" -> {
                                                        filterState.ratingRange=1.0f..2.0f
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    "2.0 - 3.0" -> {
                                                        filterState.ratingRange=2.0f..3.0f
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    "3.0 - 4.0" -> {
                                                        filterState.ratingRange=3.0f..4.0f
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    "4.0 +" -> {
                                                        filterState.ratingRange=4.0f..5.0f
                                                        updateSelected(selectedFilters, filterState)
                                                        viewModel.filterProducts(filterState)
                                                    }

                                                    else -> {}
                                                }
                                            },
                                            context = context,
                                            onCloseClick = {
                                                scope.launch {
                                                    sheetState.hide()
                                                }
                                            })
                                    }
                                    scope.launch { sheetState.show() }
                                } else {

                                }
                            }
                        },
                        isSelected = selectedFilters,
                    )

                }
            }
        }
    }
}
