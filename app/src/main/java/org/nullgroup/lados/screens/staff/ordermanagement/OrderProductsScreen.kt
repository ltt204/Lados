package org.nullgroup.lados.screens.staff.ordermanagement

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.compose.common.DefaultCenterTopAppBar
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.common.TwoColsItem
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.getActionForButtonOfOrder
import org.nullgroup.lados.utilities.getStatusByName
import org.nullgroup.lados.utilities.toCurrency
import org.nullgroup.lados.viewmodels.staff.order.OrderProductsState
import org.nullgroup.lados.viewmodels.staff.order.OrderProductsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderProductsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    orderProductsViewModel: OrderProductsViewModel = hiltViewModel(),
) {
    val productVariants by orderProductsViewModel.productVariantState.collectAsState()
    val orderProducts by orderProductsViewModel.orderProducts.collectAsState()

    Scaffold(
        topBar = {
            DefaultCenterTopAppBar(
                onBackClick = {
                    navController.navigateUp()
                },
                content = "Order Products",
                modifier = Modifier.padding(LadosTheme.size.medium)
            )
        },
        modifier = modifier
            .background(
                color = LadosTheme.colorScheme.background,
            )
    ) { innerPadding ->
        when (productVariants) {
            is OrderProductsState.Error -> {

            }

            OrderProductsState.Loading -> {
                LoadOnProgress(
                    modifier = modifier.fillMaxSize(),
                    content = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(LadosTheme.size.extraExtraLarge)
                        )
                    }
                )
            }

            is OrderProductsState.Success -> {
                val currentOrder by orderProductsViewModel.currentOrder.collectAsState()

                OrderProductsView(
                    modifier = Modifier.padding(
                        top = innerPadding.calculateTopPadding(),
                        start = LadosTheme.size.medium,
                        end = LadosTheme.size.medium
                    ),
                    orderProducts = orderProducts,
                    productVariants = (productVariants as OrderProductsState.Success).products,
                    orderStatus = getStatusByName(
                        currentOrder
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
    productVariants: List<Pair<Product, ProductVariant>>,
    orderStatus: OrderStatus,
    navController: NavController,
    modifier: Modifier = Modifier,
    orderProducts: List<OrderProduct> = emptyList(),
    context: Context = LocalContext.current,
) {
    val buttonAction = orderStatus.getActionForButtonOfOrder(context)

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LadosTheme.size.medium),
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
    product: Product,
    variant: ProductVariant,
    orderProduct: OrderProduct,
    navController: NavController,
    modifier: Modifier = Modifier,
    buttonAction: Pair<String?, (NavController, String?, String?) -> Unit>,
) {
    val image = variant.images.firstOrNull()?.link

    Card(
        modifier = modifier,
        onClick = {
            navController.navigate("${Screen.Customer.ProductDetailScreen.route}/${product.id}")
        }
    ) {
        TwoColsItem(
            content = {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(LadosTheme.shape.medium),
                        model = ImageRequest.Builder(LocalContext.current)
                            .crossfade(true)
                            .data(image)
                            .build(),
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Crop,
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(
                                    LadosTheme.size.normal,
                                )
                            )
                        },
                        error = {
                            Text(
                                text = "Image failed to load ${it.result.throwable.message}"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.padding(horizontal = LadosTheme.size.small))

                    Column(
                        modifier = Modifier.fillMaxHeight(),
                    ) {
                        Text(
                            text = product.name,
                            style = LadosTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            )
                        )

                        Column {
                            Text(
                                text = "Size: ${variant.size.sizeName}",
                                style = LadosTheme.typography.bodySmall,
                            )
                            Text(
                                text = "Color: ${variant.color.colorName}",
                                style = LadosTheme.typography.bodySmall,
                            )
                            Text(
                                text = "Qty: ${orderProduct.amount}",
                                style = LadosTheme.typography.bodySmall,
                            )
                        }

                        Column {
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
            onClick = {
                navController.navigate("${Screen.Customer.ProductDetailScreen.route}/${product.id}")
            }
        )
    }
}