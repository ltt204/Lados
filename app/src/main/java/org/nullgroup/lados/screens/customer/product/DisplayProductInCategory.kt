package org.nullgroup.lados.screens.customer.product

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.outlined.List
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.data.local.SearchHistoryManager
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.BottomSheetContent
import org.nullgroup.lados.screens.customer.DrawError_FindNotMatch
import org.nullgroup.lados.screens.customer.ProductItem
import org.nullgroup.lados.screens.customer.SearchBar
import org.nullgroup.lados.screens.customer.SearchBarRow
import org.nullgroup.lados.screens.customer.hasNoSalePrice
import org.nullgroup.lados.screens.customer.sumOfSaleAmount
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.OnSurface
import org.nullgroup.lados.ui.theme.Outline
import org.nullgroup.lados.ui.theme.Primary
import org.nullgroup.lados.ui.theme.SurfaceContainerHighest
import org.nullgroup.lados.viewmodels.HomeViewModel
import org.nullgroup.lados.viewmodels.ProductUiState
import org.nullgroup.lados.viewmodels.SharedViewModel


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
    isSelected: List<Boolean> = listOf(false,false,false,false,false),
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
                        0 -> if (inAllProducts) FilterButton(
                            text = "Category",
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Category Filter",
                            type = "Category",
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[3],
                        )

                        1 -> if (!inOnSale) FilterButton(
                            text = "On Sale",
                            contentDescription = "On Sale Filter",
                            type = "Deals",
                            onButtonClick = onNormalButtonClick,
                            isSelected = isSelected[0],
                        )

                        2 -> FilterButton(
                            text = "Price",
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Price Sort",
                            type = "Price",
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[1]
                        )

                        3 -> FilterButton(
                            text = "Sort by",
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Sort by",
                            type = "Sort by",
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[2]
                        )

                        4 -> FilterButton(
                            text = "Rating",
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Rating Filter",
                            type = "Rating",
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[4],
                        )

                        5-> FilterButton(
                            text="Pricing Range",
                            icon = Icons.Outlined.KeyboardArrowDown,
                            type = "Pricing Range",
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[5],
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
            // Add any other desired styling or configuration
        )

        // Display labels for selected values
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${sliderPosition.start.toInt()} ", // Start value label
                modifier = Modifier.weight(1f),
                color = LadosTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(10.dp)) // 10 spaces between labels
            Text(
                text = " ${sliderPosition.endInclusive.toInt()}", // End value label
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
    var selectedSortOption by remember { mutableStateOf<String?>(null) }
    var isSelected by remember { mutableStateOf(listOf(false, false, false, false, false, false)) }
    val typeScreen = sharedViewModel.typeScreen

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
        "Rating" to listOf("1.0 to 2.0", "2.0 to 3.0", "3.0 to 4.0", "4.0 +")
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
                            val oldStatus: List<Boolean> = isSelected
                            if (selectedSortOption == "On Sale") {
                                selectedSortOption = null
                                viewModel.resetProducts()
                                isSelected = listOf(false, oldStatus[1], oldStatus[2], oldStatus[3], oldStatus[4] )
                            } else {
                                selectedSortOption = "On Sale"
                                viewModel.filterSaleProducts()
                                isSelected = listOf(true, oldStatus[1], false, oldStatus[3], oldStatus[4])
                            }
                        },
                        onButtonClick = { content ->
                            Log.d("FilterButton", "onButtonClick: $content")
                            if (content=="Pricing Range") {
                                sheetContent = {
                                    BottomSheetContent(
                                        title = content,
                                        paddingValues = paddingValues,
                                        onClearClick = {
                                            isSelected = listOf(
                                                selectedSortOption == "On Sale",
                                                isSelected[1],
                                                isSelected[2],
                                                isSelected[3],
                                                isSelected[4],
                                                false
                                            )
                                        },
                                        onSliderChanged = { sliderRange ->
                                            viewModel.resetProducts()
                                            viewModel.filterProductsByPrice(
                                                sliderRange.start,
                                                sliderRange.endInclusive
                                            )
                                            isSelected = listOf(
                                                selectedSortOption == "On Sale",
                                                true,
                                                false,
                                                isSelected[3],
                                                isSelected[4],
                                                true
                                            )
                                        },
                                        onCloseClick = {
                                            scope.launch {
                                                sheetState.hide()
                                            }
                                        },
                                        options = emptyList(),
                                    )
                                }
                                scope.launch { sheetState.show() }
                            }
                            else {
                                val option = optionsMap[content] ?: emptyList()
                                if (option.isNotEmpty()) {
                                    sheetContent = {
                                        BottomSheetContent(
                                            title = content,
                                            options = option,
                                            paddingValues = paddingValues,
                                            onClearClick = { clearOption ->
                                                when (clearOption) {
                                                    "Sort by" -> {
                                                        val oldStatus = isSelected[1]
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            oldStatus,
                                                            false,
                                                            isSelected[3],
                                                            isSelected[4],
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "Price" -> {
                                                        val oldStatus = isSelected[2]
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            false,
                                                            oldStatus,
                                                            isSelected[3],
                                                            isSelected[4],
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "Category" -> {
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            isSelected[1],
                                                            isSelected[2],
                                                            false,
                                                            isSelected[4],
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "Rating" -> {
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            isSelected[1],
                                                            isSelected[2],
                                                            isSelected[3],
                                                            false,
                                                            isSelected[5]
                                                        )
                                                    }
                                                }
                                                viewModel.resetProducts()
                                            },
                                            onSelectionChanged = { selectedOption ->
                                                Log.d(
                                                    "FilterButton",
                                                    "onSelectionChanged: $selectedOption"
                                                )
                                                when (selectedOption) {
                                                    "Lowest - Highest Price" -> {
                                                        viewModel.sortProductsByPriceLowToHigh()
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            true,
                                                            false,
                                                            isSelected[3],
                                                            isSelected[4],
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "Highest - Lowest Price" -> {
                                                        viewModel.sortProductsByPriceHighToLow()
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            true,
                                                            false,
                                                            isSelected[3],
                                                            isSelected[4],
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "Recommended" -> {
                                                        viewModel.resetProducts()
                                                        isSelected = listOf(
                                                            false,
                                                            false,
                                                            true,
                                                            isSelected[3],
                                                            isSelected[4],
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "Newest" -> {
                                                        viewModel.sortProductsByCreatedAt()
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            false,
                                                            true,
                                                            isSelected[3],
                                                            isSelected[4],
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "Pant" -> {
                                                        viewModel.resetProducts()
                                                        viewModel.findCategoryByName("Pant")
                                                            ?.let { it1 ->
                                                                viewModel.filterProductsByCategory(
                                                                    it1
                                                                )
                                                            }
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            false,
                                                            false,
                                                            true,
                                                            isSelected[4],
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "Crop-top" -> {
                                                        viewModel.resetProducts()
                                                        viewModel.findCategoryByName("Crop-top")
                                                            ?.let { it1 ->
                                                                viewModel.filterProductsByCategory(
                                                                    it1
                                                                )
                                                            }
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            false,
                                                            false,
                                                            true,
                                                            isSelected[4],
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "Top" -> {
                                                        viewModel.resetProducts()
                                                        viewModel.findCategoryByName("Top")
                                                            ?.let { it1 ->
                                                                viewModel.filterProductsByCategory(
                                                                    it1
                                                                )
                                                            }
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            false,
                                                            false,
                                                            true,
                                                            isSelected[4],
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "1.0 to 2.0" -> {
                                                        viewModel.resetProducts()
                                                        viewModel.filterProductsByRating(
                                                            1.0f,
                                                            2.0f
                                                        )
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            false,
                                                            false,
                                                            false,
                                                            true,
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "2.0 to 3.0" -> {
                                                        viewModel.resetProducts()
                                                        viewModel.filterProductsByRating(
                                                            2.0f,
                                                            3.0f
                                                        )
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            false,
                                                            false,
                                                            false,
                                                            true,
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "3.0 to 4.0" -> {
                                                        viewModel.resetProducts()
                                                        viewModel.filterProductsByRating(
                                                            3.0f,
                                                            4.0f
                                                        )
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            false,
                                                            false,
                                                            false,
                                                            true,
                                                            isSelected[5]
                                                        )
                                                    }

                                                    "4.0 +" -> {
                                                        viewModel.resetProducts()
                                                        viewModel.filterProductsByRating(
                                                            4.0f,
                                                            5.0f
                                                        )
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            false,
                                                            false,
                                                            false,
                                                            true,
                                                            isSelected[5]
                                                        )
                                                    }
                                                }
                                            },
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
                        isSelected = isSelected,
                    )

                }
            }
        }
    }
}

/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReviewProductInCategoryScreen() {
    LadosTheme {
        ProductInCategoryScreen(navController = NavController(LocalContext.current))
    }
}
 */