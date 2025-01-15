package org.nullgroup.lados.screens.customer.order

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.common.DefaultCenterTopAppBar
import org.nullgroup.lados.compose.common.TwoColsItem
import org.nullgroup.lados.data.models.Color
import org.nullgroup.lados.data.models.Image
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.models.Size
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.getActionForButtonOfOrderProduct
import org.nullgroup.lados.utilities.getStatusByName
import org.nullgroup.lados.utilities.toCurrency
import org.nullgroup.lados.viewmodels.customer.order.OrderProductsState
import org.nullgroup.lados.viewmodels.customer.order.OrderProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderProductsViewScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    navController: NavController? = null,
    orderProductsViewModel: OrderProductsViewModel = hiltViewModel(),
) {
    val productVariants = orderProductsViewModel.productVariantsState.collectAsState()
    val orderProducts = orderProductsViewModel.orderProducts.collectAsState()
    Log.d("OrderProductsViewScreen", "OrderProductsViewScreen: $orderProducts")
    Scaffold(
        modifier = modifier,
        topBar = {
            DefaultCenterTopAppBar(
                onBackClick = {
                    navController?.navigateUp()
                },
                content = stringResource(id = R.string.order_products_title)
            )
        },
        backgroundColor = LadosTheme.colorScheme.background
    ) { innerPadding ->
        when (productVariants.value) {
            is OrderProductsState.Loading -> {
                LoadOnProgress(
                    modifier = modifier.fillMaxSize(),
                    content = {
                        CircularProgressIndicator(modifier = Modifier.size(LadosTheme.size.extraExtraLarge))
                    }
                )
            }

            is OrderProductsState.Error -> {
                /*TODO: Handle Error*/
            }

            is OrderProductsState.Success -> {
                val currentOrder = orderProductsViewModel.currentOrder.collectAsState()
                OrderProductsView(
                    modifier = Modifier.padding(
                        top = innerPadding.calculateTopPadding(),
                        start = LadosTheme.size.medium,
                        end = LadosTheme.size.medium
                    ),
                    orderProducts = orderProducts.value,
                    productVariants = (productVariants.value as OrderProductsState.Success).orderProducts,
                    orderStatus = getStatusByName(
                        currentOrder.value
                            .orderStatusLog.entries.last().key
                    ),
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun OrderProductsView(
    modifier: Modifier = Modifier,
    productVariants: List<Pair<Product, ProductVariant>>,
    orderProducts: List<OrderProduct> = emptyList(),
    orderStatus: OrderStatus,
    navController: NavController? = null,
    context: Context = LocalContext.current
) {
    val buttonAction = orderStatus.getActionForButtonOfOrderProduct(context)

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LadosTheme.size.medium)
    ) {
        items(items = productVariants, key = { it.first.id }) { (product, variant) ->
            val orderProduct = orderProducts.find { it.productId == product.id }
            OrderProductItem(
                modifier = Modifier.heightIn(min = 72.dp, max = 128.dp),
                product = product,
                variant = variant,
                orderProduct = orderProduct!!,
                navController = navController,
                buttonAction = buttonAction
            )
        }
    }
}


@Composable
fun OrderProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    variant: ProductVariant,
    orderProduct: OrderProduct,
    navController: NavController? = null,
    buttonAction: Pair<String?, (NavController, String?, String?) -> Unit>,
) {
    val image = variant.images.firstOrNull()?.link
    Card(
        modifier = modifier,
        onClick = {
        }
    ) {
        TwoColsItem(
            content = {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(LadosTheme.shape.medium),
                        model = ImageRequest
                            .Builder(LocalContext.current)
                            .crossfade(true)
                            .data(image)
                            .build(),
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Crop,
                        loading = {
                            CircularProgressIndicator(modifier = Modifier.size(LadosTheme.size.medium))
                        },
                        error = {
                            Text(text = "Image failed to load: ${it.result.throwable.message}")
                        }
                    )
                    Spacer(modifier = Modifier.padding(horizontal = LadosTheme.size.small))
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                    ) {
                        Text(
                            text = product.name,
                            style = LadosTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        )
                        Column {
                            Text(
                                text = "Size: ${variant.size.sizeName}",
                                style = LadosTheme.typography.bodySmall
                            )
                            Text(
                                text = "Color: ${variant.color.colorName}",
                                style = LadosTheme.typography.bodySmall
                            )
                            Text(
                                text = "Qty: ${orderProduct.amount}",
                                style = LadosTheme.typography.bodySmall
                            )
                        }
                        Column(
                            modifier = Modifier,
                        ) {
                            val amount = orderProduct.amount
                            val isSale = variant.salePrice != null
                            Text(
                                text = (variant.originalPrice * amount).toCurrency(),
                                textDecoration = if (isSale) TextDecoration.LineThrough else null,
                                style = LadosTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            )
                            if (isSale) {
                                Text(
                                    text = (variant.salePrice!! * amount).toCurrency(),
                                    style = LadosTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = LadosTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    ),
                                )
                            }
                        }
                    }
                }
            },
            trailingAction = {
                buttonAction.first?.let {
                    TextButton(onClick = {
                        buttonAction.second.invoke(
                            navController!!,
                            product.id,
                            variant.id
                        )
                    }) {
                        Text(
                            text = it,
                            style = LadosTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            ),
                        )
                    }
                }
            },
            onClick = {
                buttonAction.second.invoke(
                    navController!!,
                    product.id,
                    variant.id
                )
            }
        )
    }
}

@Preview
@Composable
fun OrderProductItemPreview() {
    // Sample test data
    val product = Product(
        id = "product1",
        name = "Test Product 1",
        variants = listOf(
            ProductVariant(
                id = "variant1",
                size = Size(1, "Small"),
                color = Color(1, "Red", "#FF0000"),
                originalPrice = 29.99,
                images = listOf(
                    Image(
                        id = "image1",
                        link = "https://via.placeholder.com/150"
                    )
                )
            )
        )
    )
    val variant = ProductVariant(
        id = "variant1",
        size = Size(1, "Small"),
        color = Color(1, "Red", "#FF0000"),
        originalPrice = 29.99,
        images = listOf(
            Image(
                id = "image1",
                link = "https://via.placeholder.com/150"
            )
        )
    )

    OrderProductItem(
        Modifier,
        product,
        variant,
        OrderProduct(
            id = "orderProduct1",
            productId = "product1",
            variantId = "variant1",
            amount = 2,
            totalPrice = 59.98
        ),
        buttonAction = Pair("Action") { navController, _, _ -> /*TODO*/ }
    )
}