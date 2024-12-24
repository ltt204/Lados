package org.nullgroup.lados.screens.customer.product

import android.annotation.SuppressLint
import android.content.Context
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
    isSelected: List<Boolean> = listOf(false,false,false),
    inCategory: Boolean = false,
    inNewest: Boolean=false,
    inTopSelling: Boolean=false,
    inSearch: Boolean=false,
) {
    if (inSearch && products.none { it.name.contains(sharedViewModel.searchQuery ?: "") }) {
        DrawError_FindNotMatch(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .padding(top = paddingValues.calculateTopPadding()),
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
                items(3) { index ->
                    when (index) {
                        0 -> FilterButton(
                            text = "On Sale",
                            contentDescription = "On Sale Filter",
                            type = "Deals",
                            onButtonClick = onNormalButtonClick,
                            isSelected = isSelected[0],
                        )

                        1 -> FilterButton(
                            text = "Price",
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Price Sort",
                            type = "Price",
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[1]
                        )

                        2 -> FilterButton(
                            text = "Sort by",
                            icon = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Sort by",
                            type = "Sort by",
                            onButtonClick = onButtonClick,
                            isNormal = false,
                            isSelected = isSelected[2]
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
                    products.filter { it.engagements.size >= 2 }.take(10)
                else if (inNewest)
                    products.sortedByDescending { it.createdAt }
                else if (inCategory)
                    products.filter { it.categoryId == sharedViewModel.sharedData?.categoryId }
                else if (inSearch)
                    products.filter { it.name.contains(sharedViewModel.searchQuery ?: "") }
                else products
            )
        }
    }
}

@Composable
fun ProductInCategoryScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    sharedViewModel: SharedViewModel = SharedViewModel(),
    viewModel: HomeViewModel = hiltViewModel(),
    context: Context
) {
    val productUiState = viewModel.productUiState.collectAsStateWithLifecycle().value
    var selectedSortOption by remember { mutableStateOf<String?>(null) }
    var isSelected by remember { mutableStateOf(listOf(false, false, false)) }
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
        "Price" to listOf("Lowest - Highest Price", "Highest - Lowest Price")
    )
    ModalBottomSheetLayout(
        sheetState = sheetState,
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
                .padding(bottom = paddingValues.calculateBottomPadding())
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
                            onNormalButtonClick = { _ ->
                                val oldStatus: List<Boolean> = isSelected
                                if (selectedSortOption == "On Sale") {
                                    selectedSortOption = null
                                    viewModel.resetProducts()
                                    isSelected = listOf(false, oldStatus[1], oldStatus[2])
                                } else {
                                    selectedSortOption = "On Sale"
                                    viewModel.filterSaleProducts()
                                    isSelected = listOf(true, oldStatus[1], false)
                                }
                            },
                            onButtonClick = { content ->
                                val option = optionsMap[content] ?: emptyList()
                                if (option.isNotEmpty()) {
                                    sheetContent = {
                                        BottomSheetContent(
                                            title = content,
                                            options = option,
                                            paddingValues = paddingValues,
                                            onClearClick = { clearOption ->

                                                if (clearOption == "Sort by") {
                                                    val oldStatus = isSelected[1]
                                                    isSelected = listOf(
                                                        selectedSortOption == "On Sale",
                                                        oldStatus,
                                                        false,
                                                    )
                                                } else if (clearOption == "Price") {
                                                    val oldStatus = isSelected[2]
                                                    isSelected = listOf(
                                                        selectedSortOption == "On Sale",
                                                        false,
                                                        oldStatus
                                                    )
                                                }
                                                viewModel.resetProducts()
                                            },
                                            onSelectionChanged = { selectedOption ->
                                                when (selectedOption) {
                                                    "Lowest - Highest Price" -> {
                                                        viewModel.sortProductsByPriceLowToHigh()
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            true,
                                                            false
                                                        )
                                                    }

                                                    "Highest - Lowest Price" -> {
                                                        viewModel.sortProductsByPriceHighToLow()
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            true,
                                                            false
                                                        )
                                                    }

                                                    "Recommended" -> {
                                                        viewModel.resetProducts()
                                                        isSelected = listOf(false, false, true)
                                                    }

                                                    "Newest" -> {
                                                        viewModel.sortProductsByCreatedAt()
                                                        isSelected = listOf(
                                                            selectedSortOption == "On Sale",
                                                            false,
                                                            true
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