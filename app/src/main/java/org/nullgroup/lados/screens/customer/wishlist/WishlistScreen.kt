package org.nullgroup.lados.screens.customer.wishlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.data.models.Image
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.home.ProductItem
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.customer.wishlist.WishlistUiState
import org.nullgroup.lados.viewmodels.customer.wishlist.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    navController: NavController,
    innerPadding: PaddingValues = PaddingValues(bottom = 0.dp),
    modifier: Modifier = Modifier
) {
    val wishlistViewModel: WishlistViewModel = hiltViewModel()

    val wishlistUiState = wishlistViewModel.wishlistUiState.collectAsStateWithLifecycle()
    val currentItems = when (val state = wishlistUiState.value) {
        is WishlistUiState.Success -> state.items
        else -> emptyList()
    }

    val productInfo = wishlistViewModel.productInformation.collectAsStateWithLifecycle()
    val currentItemsWithInfo = currentItems.filter {
        productInfo.value?.containsKey(it.productId) == true
    }

    val placeholderProduct = Product(
        variants = listOf(ProductVariant(
            images = listOf(Image(
                link = "https://placehold.co/600x400",
            ))
        ))
    )
    val productInfoOf = { productId: String ->
        productInfo.value?.get(productId) ?: placeholderProduct
    }

    val onNavigateBack = {
        navController.popBackStack()
    }

    val onProductClick = { productId : String ->
        navController.navigate(Screen.Customer.ProductDetailScreen.route + "/$productId")
    }

    val onWishlistItemRemove = { productId : String ->
        wishlistViewModel.removeWishlistItem(productId)
    }

    LaunchedEffect(Unit) {
        wishlistViewModel.fetchProductInfo()
    }

    val iconButtonColors = IconButtonColors(
        contentColor = LadosTheme.colorScheme.onSecondaryContainer,
        containerColor = LadosTheme.colorScheme.secondaryContainer,
        disabledContentColor = LadosTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.38f),
        disabledContainerColor = LadosTheme.colorScheme.secondaryContainer.copy(alpha = 0.38f),
    )
    val iconTintColor = LadosTheme.colorScheme.onSecondaryContainer
    val topAppBarColor = TopAppBarColors(
        containerColor = LadosTheme.colorScheme.surfaceContainerLowest,
        scrolledContainerColor = LadosTheme.colorScheme.surfaceContainerLow,
        navigationIconContentColor = LadosTheme.colorScheme.onSurface,
        actionIconContentColor = LadosTheme.colorScheme.onSurface,
        titleContentColor = LadosTheme.colorScheme.onSurface,
    )

    Scaffold(
        containerColor = LadosTheme.colorScheme.surfaceContainerLowest,
        modifier = modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.profile_wishlist),
                        textAlign = TextAlign.Center,
                        style = LadosTheme.typography.titleLarge.copy(
                            color = LadosTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigateBack() },
//                        enabled = isAllowedInteracting,
                        colors = iconButtonColors,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.wishlist_back_description),
                            tint = iconTintColor,
                        )
                    }
                },
                colors = topAppBarColor,
            )
        },
    ) { innerScaffoldPadding ->

        when (wishlistUiState.value) {
            is WishlistUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(innerScaffoldPadding)
                        .padding(horizontal = 20.dp)
                        .fillMaxSize()
                        .background(LadosTheme.colorScheme.background)
                ) {
                    CircularProgressIndicator(
                        color = LadosTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(64.dp)
                    )
                }
            }
            is WishlistUiState.Success -> {
                if (currentItems.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(innerScaffoldPadding)
                            .padding(horizontal = 20.dp)
                            .fillMaxSize()
                            .background(LadosTheme.colorScheme.background),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.love),
                            colorFilter = ColorFilter.tint(LadosTheme.colorScheme.onSurface),
                            contentDescription = stringResource(R.string.wishlist_empty_description),
                            modifier = Modifier
                                .scale(2.0f)
                                .width(120.dp)
                            ,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.wishlist_empty),
                            textAlign = TextAlign.Center,
                            style = LadosTheme.typography.bodyLarge.copy(
                                color = LadosTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                lineHeight = 40.sp
                            )
                        )
                    }
                } else if (currentItemsWithInfo.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(innerScaffoldPadding)
                            .padding(horizontal = 20.dp)
                            .fillMaxSize()
                            .background(LadosTheme.colorScheme.background)
                    ) {
                        CircularProgressIndicator(
                            color = LadosTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .width(64.dp)
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(140.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .padding(innerScaffoldPadding)
                            .padding(horizontal = 20.dp)
                            .fillMaxSize()
                    ) {
                        itemsIndexed(currentItemsWithInfo) { _, wishlistItem ->
                            ProductItem(
                                product = productInfoOf(wishlistItem.productId),
                                onClick = onProductClick,
                                isClicked = true,
                                onFavicon = onWishlistItemRemove
                            )
                        }
                    }
                }
            }
            is WishlistUiState.Error -> {
                // Error
            }
        }



    }
}