package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetLayout
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.ShoppingCart
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.OnSurface
import org.nullgroup.lados.ui.theme.Outline
import org.nullgroup.lados.ui.theme.Primary
import org.nullgroup.lados.ui.theme.SurfaceContainerHighest
import org.nullgroup.lados.ui.theme.Tertiary
import org.nullgroup.lados.viewmodels.CategoryUiState
import org.nullgroup.lados.viewmodels.HomeViewModel
import org.nullgroup.lados.viewmodels.ProductUiState
import org.nullgroup.lados.viewmodels.SharedViewModel

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
                    "Search",
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
                focusedContainerColor = LadosTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
                disabledContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
                focusedBorderColor = LadosTheme.colorScheme.primary, // Consider renaming
                unfocusedBorderColor = LadosTheme.colorScheme.outline, // Consider renaming
                disabledBorderColor = LadosTheme.colorScheme.outline,
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
    color: Color = LadosTheme.colorScheme.onBackground,
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
) {
    Box(modifier = modifier
        .width(160.dp)
        .height(280.dp)
        .clip(RoundedCornerShape(8.dp))
        // note: modify
        .background(LadosTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f))
        .padding(bottom = 8.dp)
        .clickable { onClick(product.id) }
    ) {
        AsyncImage(
            model = product.variants.first().images.first().link,
            contentDescription = "Image",
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop,
        )

        Image(
            painter = painterResource(R.drawable.favicon),
            contentDescription = "Image",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = product.name,
                style = TextStyle(
                    fontSize = 16.sp,
                    // note: modify
                    color = LadosTheme.colorScheme.onBackground,
                )
            )
            Row(

                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$${product.variants.first().salePrice}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        // note: modify
                        color = LadosTheme.colorScheme.onBackground,
                    )
                )
                Text(
                    text = "$${product.variants.first().originalPrice}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = LadosTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f,
                        ),
                        textDecoration = TextDecoration.LineThrough
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
        contentPadding = PaddingValues(8.dp),
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
            Column(
                modifier = modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = paddingValues.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth(),
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
                            contentLeft = "Categories",
                            contentRight = "See all",
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
                            contentLeft = "Top Selling",
                            contentRight = "See all",
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
                            products = (productUiState.value as ProductUiState.Success).products.filter { it.engagements.size >= 2 }
                                .take(5)
                        )
                    }

                    item {
                        TitleTextRow(
                            contentLeft = "New In",
                            contentRight = "See all",
                            color = LadosTheme.colorScheme.primary,
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
                }
            }
        }
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
) {
    var selectedButtonIndex by remember { mutableStateOf<Int?>(null) }
    Box(
        modifier = Modifier
            .fillMaxHeight(0.5f)
            // note: modify
            .background(LadosTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(16.dp))
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 8.dp,
                bottom = paddingValues.calculateBottomPadding() + 8.dp
            )
    ) {
        Column(
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
                        if (title == "Sort by" && selectedButtonIndex!! >= 2) {
                            selectedButtonIndex = null
                            onClearClick("Sort by")
                        } else
                            if (title != "Sort by" && selectedButtonIndex!! < 2) {
                                selectedButtonIndex = null
                                onClearClick("Price")
                            }
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
                        fontWeight = FontWeight.Bold
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
            options.forEachIndexed { index, option ->
                Button(
                    onClick = {
                        selectedButtonIndex = index
                        if (title == "Sort by")
                            selectedButtonIndex = selectedButtonIndex!! + 2
                        onCloseClick()
                        onSelectionChanged(option)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        if ((title != "Sort by" && selectedButtonIndex == index) ||
                            (title == "Sort by" && selectedButtonIndex == index + 2)
                        )
                            LadosTheme.colorScheme.primary else
                            LadosTheme.colorScheme.outline.copy(alpha = 0.3f)
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
                            color = if ((title != "Sort by" && selectedButtonIndex == index) ||
                                (title == "Sort by" && selectedButtonIndex == index + 2)
                            )
                                LadosTheme.colorScheme.surfaceContainerHighest
                            else LadosTheme.colorScheme.onSurface
                        )
                        if ((title != "Sort by" && selectedButtonIndex == index) ||
                            (title == "Sort by" && selectedButtonIndex == index + 2)
                        )
                            Icon(
                                Icons.Outlined.Done,
                                contentDescription = null
                            )
                    }
                }
            }
        }
    }
}


@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    sharedViewModel: SharedViewModel = SharedViewModel(),
    viewModel: HomeViewModel = hiltViewModel(),
) {
    Scaffold(
        modifier = modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        topBar = {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 16.dp, horizontal = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "Back",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp)
                    )
                }

                Text(
                    text = "Lados",
                    style = LadosTheme.typography.headlineSmall.copy(
                        color = LadosTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

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

