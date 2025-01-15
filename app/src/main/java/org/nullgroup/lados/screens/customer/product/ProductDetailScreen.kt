package org.nullgroup.lados.screens.customer.product

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.data.models.Image
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.Size
import org.nullgroup.lados.data.models.UserEngagement
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.formatToRelativeTime
import org.nullgroup.lados.utilities.toCurrency
import org.nullgroup.lados.viewmodels.customer.chat.ChatViewModel
import org.nullgroup.lados.viewmodels.customer.chat.events.ChatScreenEvent
import org.nullgroup.lados.viewmodels.customer.product.ProductDetailScreenViewModel
import org.nullgroup.lados.viewmodels.customer.wishlist.SingleItemWishlistUiState
import org.nullgroup.lados.viewmodels.customer.wishlist.SingleItemWishlistViewModel
import java.util.Locale

data class ProductDetailUiState(
    val product: Product = Product(),
    val sortedColors: List<org.nullgroup.lados.data.models.Color> = emptyList(),
    val sortedSizes: List<Size> = emptyList(),
    val selectedSize: Size? = null,
    val selectedColor: org.nullgroup.lados.data.models.Color? = null,
    val quantity: Int = 1,
    val quantityInStock: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
)


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductDetailScreen(
    productViewModel: ProductDetailScreenViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    wishlistViewModel: SingleItemWishlistViewModel = hiltViewModel(),
    productId: String,
    navController: NavController,
) {

    val scrollState = rememberScrollState()
    var showSizeBottomSheet by remember { mutableStateOf(false) }
    var showColorBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val uiState by productViewModel.uiState.collectAsState()

    // TODO: Adjust as you wish
    val onAddedToCart: () -> Unit = {
        Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show()
    }

    val onAddedToCartFailed: () -> Unit = {
        Toast.makeText(context, "Failed to add product to cart", Toast.LENGTH_SHORT).show()
    }
    val onAddToCart = productViewModel.onAddToCartClicked(onAddedToCart, onAddedToCartFailed)

    val wishlistUiState = wishlistViewModel.uiState.collectAsState().value
    val isFavorite = when (wishlistUiState) {
        is SingleItemWishlistUiState.Success -> wishlistUiState.isInWishList
        else -> null
    }
    val onToggleFavorite = wishlistViewModel.toggleWishList

    // Load product details on initial composition
    LaunchedEffect(productId) {
        productViewModel.getProductById(productId)

        wishlistViewModel.checkIfInWishList(productId)
    }

    when {
        uiState.isLoading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(LadosTheme.colorScheme.background)
            ) {
                CircularProgressIndicator(color = LadosTheme.colorScheme.primary)
            }
        }

        uiState.error != null -> {
            // Error handling UI
            Text("Error: ${uiState.error}")
        }

        else -> {
            Scaffold(
                topBar = {
                    ProductDetailTopBar(
                        isFavorite = isFavorite,
                        onToggleFavorite = { onToggleFavorite() },
                        onNavigateBack = { navController.navigateUp() },
                        onChat = {
                            chatViewModel.handleEvent(ChatScreenEvent.SendProduct(productId))
                            navController.navigate(Screen.Customer.ChatScreen.route)
                        }
                    )
                },
                containerColor = LadosTheme.colorScheme.background,
                bottomBar = {
                    val showPrice = uiState.product.variants.first().salePrice
                        ?: uiState.product.variants.first().originalPrice
                    ProductDetailBottomBar(
                        title = stringResource(R.string.add_to_cart),
                        enabled = uiState.quantityInStock > 0,
                        price = showPrice.toCurrency(),
                        onClick = onAddToCart
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LadosTheme.colorScheme.background)
                        .padding(paddingValues)
                        .padding(bottom = 10.dp)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState)
                ) {
                    ProductInformationSection(
                        name = uiState.product.name,
                        originalPrice = uiState.product.variants.first().originalPrice,
                        salePrice = uiState.product.variants.first().salePrice,
                        productImages = uiState.product.variants.first().images,
                        quantityInStock = uiState.quantityInStock
                    )

                    ProductDetailsSection(
                        onSizeClick = { showSizeBottomSheet = true },
                        onColorClick = { showColorBottomSheet = true },
                        onUpdateQuantity = {
                            productViewModel.updateQuantity(it)
                        },
                        description = uiState.product.description,
                        size = uiState.selectedSize?.sizeName ?: "",
                        color = uiState.selectedColor ?: org.nullgroup.lados.data.models.Color()
                    )

                    ProductReviewSection(
                        engagements = uiState.product.engagements.sortedByDescending {
                            it.createdAt
                        },
                        averageRating = uiState.product.engagements.map {
                            it.ratings
                        }.average(),
                        numOfReviews = uiState.product.engagements.size
                    )
                }

                // Bottom Sheets
                SelectionBottomSheet(
                    title = "Select Size",
                    showBottomSheet = showSizeBottomSheet,
                    onDismiss = { showSizeBottomSheet = false }
                ) {
                    SelectionList(
                        items = uiState.sortedSizes,
                        itemContent = { it.sizeName },
                        itemColor = { Color.Transparent },
                        initialSelection = uiState.sortedSizes.indexOf(uiState.selectedSize),
                        onItemSelected = {
                            productViewModel.updateSelectedSize(it)
                            productViewModel.updateQuantityInStock()
                        }
                    )
                }

                SelectionBottomSheet(
                    title = "Select Color",
                    showBottomSheet = showColorBottomSheet,
                    onDismiss = { showColorBottomSheet = false }
                ) {
                    SelectionList(
                        items = uiState.sortedColors,
                        itemContent = { it.colorName },
                        itemColor = { Color(android.graphics.Color.parseColor(it.hexCode)) },
                        initialSelection = uiState.sortedColors.indexOf(uiState.selectedColor),
                        onItemSelected = {
                            productViewModel.updateSelectedColor(it)
                            productViewModel.updateQuantityInStock()
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ProductDetailTopBar(
    isFavorite: Boolean? = null,
    onToggleFavorite: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onChat: () -> Unit = {},
) {
//    var isFavorite by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(top = 16.dp)
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .clip(CircleShape)
                .background(LadosTheme.colorScheme.surfaceContainerHighest)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = LadosTheme.colorScheme.onBackground
            )
        }

        IconButton(
            onClick = { onChat() },
            modifier = Modifier
                .clip(CircleShape)
                .background(LadosTheme.colorScheme.surfaceContainerHighest)
        ) {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = "Chat with Staff",
                tint = LadosTheme.colorScheme.onBackground,
            )
        }

        IconButton(
//            onClick = { isFavorite = isFavorite.not() },
            onClick = onToggleFavorite,
            enabled = isFavorite != null,
            modifier = Modifier
                .clip(CircleShape)
                .background(LadosTheme.colorScheme.surfaceContainerHighest)
        ) {
            Icon(
                imageVector = if (isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = LadosTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ProductInformationSection(
    modifier: Modifier = Modifier,
    name: String,
    originalPrice: Double,
    salePrice: Double?,
    productImages: List<Image> = emptyList(),
    quantityInStock: Int = 0,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {

        // Image Carousel
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(productImages.size) { index ->

                SubcomposeAsyncImage(
                    model = coil.request.ImageRequest.Builder(LocalContext.current)
                        .data(productImages[index].link)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Loaded Image",
                    loading = {
                        LoadOnProgress(
                            modifier = Modifier,
                            content = { CircularProgressIndicator() }
                        )
                    },
                    modifier = Modifier
                        .height(300.dp)
                        .fillParentMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.FillHeight
                )
            }
        }

        // Product Details
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            color = LadosTheme.colorScheme.onBackground
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()


        ) {
            val isSale = salePrice != null

            if (isSale) {
                Text(
                    text = salePrice!!.toCurrency(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = LadosTheme.colorScheme.primary
                )
            }

            Text(
                text = originalPrice.toCurrency(),
                color = if (isSale) LadosTheme.colorScheme.outline else LadosTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                textDecoration = if (isSale) TextDecoration.LineThrough else TextDecoration.None,
                fontSize = if (isSale) 18.sp else 22.sp
            )

        }

        Text(
            "Quantity in stock: $quantityInStock",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = LadosTheme.colorScheme.onBackground
        )

    }
}

@Composable
fun ProductDetailsSection(
    modifier: Modifier = Modifier,
    size: String,
    color: org.nullgroup.lados.data.models.Color,
    description: String = "",
    onSizeClick: () -> Unit,
    onColorClick: () -> Unit,
    onUpdateQuantity: (Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        // Size Selection
        SelectableDetailRow(
            title = stringResource(R.string.product_size),
            currentSelection = size,
            onClick = onSizeClick
        )

        // Color Selection
        SelectableDetailRow(
            title = stringResource(R.string.product_color),
            currentSelection = color.colorName,
            additionalContent = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor(color.hexCode)))
                )
            },
            onClick = onColorClick
        )

        // Quantity Selection
        QuantitySelector(
            onUpdateQuantity = onUpdateQuantity
        )

        // Product Description
        Text(
            text = description,
            fontSize = 14.sp,
            color = LadosTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SelectableDetailRow(
    title: String,
    currentSelection: String,
    onClick: () -> Unit = {},
    additionalContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(LadosTheme.colorScheme.surfaceContainerHighest)
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            fontSize = 15.sp,
            color = LadosTheme.colorScheme.onBackground
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            additionalContent?.invoke()

            Text(
                text = currentSelection,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = LadosTheme.colorScheme.onBackground
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select $title",
                tint = LadosTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun QuantitySelector(
    modifier: Modifier = Modifier,
    onUpdateQuantity: (Int) -> Unit = {},
) {
    var quantity by remember { mutableIntStateOf(1) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(LadosTheme.colorScheme.surfaceContainerHighest)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.product_quantity),
            fontSize = 15.sp,
            color = LadosTheme.colorScheme.onBackground
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (quantity > 1) {
                        quantity--
                    }
                    onUpdateQuantity(quantity)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.minus_icon),
                    contentDescription = "Decrease Quantity",
                    tint = LadosTheme.colorScheme.primary
                )
            }

            Text(
                text = quantity.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = LadosTheme.colorScheme.onBackground
            )

            IconButton(
                onClick = {
                    quantity++
                    onUpdateQuantity(quantity)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_icon),
                    contentDescription = "Increase Quantity",
                    tint = LadosTheme.colorScheme.primary
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductReviewSection(
    engagements: List<UserEngagement>,
    averageRating: Double,
    numOfReviews: Int,
    productViewModel: ProductDetailScreenViewModel = hiltViewModel(),
) {

    LaunchedEffect(engagements) {
        val userIds = engagements.map { it.userId }
        productViewModel.fetchUsers(userIds)
    }

    val users by productViewModel.users.collectAsState()

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {

        Text(
            text = stringResource(R.string.product_reviews),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = LadosTheme.colorScheme.onBackground
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(
                    R.string.product_ratings,
                    String.format(Locale.getDefault(), "%.2f", averageRating)
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = LadosTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$numOfReviews ${stringResource(id = R.string.product_reviews)}",
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = LadosTheme.colorScheme.outline
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .height(250.dp)
        ) {
            items(items = engagements, key = { it.id }) { engagement ->
                productViewModel.getUser(engagement.userId)

                ReviewCard(
                    name = users[engagement.userId] ?: engagement.userId,
                    maxRatings = 5,
                    ratings = engagement.ratings,
                    reviews = engagement.reviews,
                    createAt = formatToRelativeTime(engagement.createdAt)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewCard(
    name: String = "",
    maxRatings: Int = 5,
    ratings: Int = 5,
    reviews: String = "",
    createAt: String = "",
) {

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(LadosTheme.colorScheme.primary)
                    )
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = LadosTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {

                    repeat(maxRatings) { index ->
                        Icon(
                            painter = painterResource(R.drawable.star_icon),
                            contentDescription = "Star",
                            tint = if (index < ratings) LadosTheme.colorScheme.primary else LadosTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = reviews,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = LadosTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                createAt,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = LadosTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun ProductDetailBottomBar(
    title: String = "",
    price: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = LadosTheme.colorScheme.primary,
            disabledContainerColor = LadosTheme.colorScheme.outline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            Text(
                text = price,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
        }
    }
}


@Composable
fun <T> SelectionList(
    items: List<T>,
    itemContent: @Composable (T) -> String,
    itemColor: @Composable (T) -> Color,
    initialSelection: Int = -1,
    onItemSelected: (T) -> Unit = {},
) {
    var selectedIndex by remember { mutableIntStateOf(initialSelection) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.size) { index ->
            SelectableItem(
                label = itemContent(items[index]),
                color = itemColor(items[index]),
                isSelected = index == selectedIndex,
                onClick = {
                    selectedIndex = index
                    onItemSelected(items[index])
                }
            )
        }
    }
}

@Composable
fun SelectableItem(
    label: String,
    color: Color = Color.Transparent,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor =
        if (isSelected) LadosTheme.colorScheme.primary else LadosTheme.colorScheme.surfaceContainerHighest
    val textColor = if (isSelected) Color.White else LadosTheme.colorScheme.onBackground

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(20.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Chosen",
                tint = textColor,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(if (isSelected) 1f else 0f)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionBottomSheet(
    title: String,
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = LadosTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth()

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.size(24.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = LadosTheme.colorScheme.onBackground
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.clickable(onClick = onDismiss),
                        tint = LadosTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                content()
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
