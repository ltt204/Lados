package org.nullgroup.lados.screens.customer.product

import android.annotation.SuppressLint
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
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.BottomSheetContent
import org.nullgroup.lados.screens.customer.ProductItem
import org.nullgroup.lados.screens.customer.SearchBarRow
import org.nullgroup.lados.ui.theme.BlackMaterial
import org.nullgroup.lados.ui.theme.GrayMaterial
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.MagentaMaterial
import org.nullgroup.lados.ui.theme.WhiteMaterial
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
    onButtonClick: (String) -> Unit = {}
) {
    var selectedButton by remember { mutableStateOf<Boolean?>(false) }
    Button(
        onClick = {
            onButtonClick(type)
            selectedButton = !selectedButton!!
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            if (selectedButton == true) MagentaMaterial else GrayMaterial.copy(
                alpha = 0.2f
            )
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(text = text, color = if (selectedButton == true) WhiteMaterial else BlackMaterial)
            if (icon != null) {
                Icon(
                    icon,
                    tint = if (selectedButton == true) WhiteMaterial else BlackMaterial,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun DrawProductInCategoryScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    textStyle: TextStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
    paddingValues: PaddingValues,
    products: List<Product> = emptyList(),
    sharedViewModel: SharedViewModel = SharedViewModel(),
    onButtonClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .padding(top = paddingValues.calculateTopPadding()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier // Apply padding here
        ) {
            items(5) { index -> // Use items() for better readability
                when (index) {
                    0 -> FilterButton(
                        text = "2",
                        icon = Icons.Outlined.List,
                        contentDescription = "Build Filter",
                        type = "Filter",
                        onButtonClick = onButtonClick
                    )

                    1 -> FilterButton(
                        text = "On Sale",
                        contentDescription = "On Sale Filter",
                        type = "Deals",
                        onButtonClick = onButtonClick
                    )

                    2 -> FilterButton(
                        text = "Price",
                        icon = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Price Sort",
                        type = "Price",
                        onButtonClick = onButtonClick
                    )

                    3 -> FilterButton(
                        text = "Sort by",
                        icon = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Sort by",
                        type = "Sort by",
                        onButtonClick = onButtonClick
                    )

                    4 -> FilterButton(
                        text = "Men",
                        icon = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Men's Filter",
                        type = "Gender",
                        onButtonClick = onButtonClick
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
    viewModel: HomeViewModel = hiltViewModel()
) {
    val productUiState = viewModel.productUiState.collectAsStateWithLifecycle().value

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val scope = rememberCoroutineScope()
    var sheetContent by remember {
        mutableStateOf<@Composable () -> Unit>({})
    }

    val optionsMap = mapOf(
        "Sort by" to listOf(
            "Recommended",
            "Newest",
            "Lowest - Highest Price",
            "Highest - Lowest Price"
        ),
        "Gender" to listOf("Man", "Women", "kids"),
        "Deals" to listOf("On sale", "Free Shipping Eligible"),
        "Price" to listOf("Min", "Max")
    )
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(
            topStart = 30.dp,
            topEnd = 30.dp
        ),
        sheetContent = { sheetContent() }
    ) {
        Scaffold(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
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
                            .background(GrayMaterial.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Search",
                            tint = BlackMaterial

                        )
                    }

                    SearchBarRow(navController = navController)
                }
            }
        ) {
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
                        onButtonClick = { content ->
                            val option = optionsMap[content] ?: emptyList()
                            sheetContent = {
                                BottomSheetContent(
                                    title = content,
                                    options = option,
                                    paddingValues = paddingValues,
                                    onSelectionChanged = {},
                                    onCloseClick = {
                                        scope.launch {
                                            sheetState.hide()
                                        }
                                    })
                            }
                            scope.launch { sheetState.show() }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReviewProductInCategoryScreen() {
    LadosTheme {
        ProductInCategoryScreen(navController = NavController(LocalContext.current))
    }
}