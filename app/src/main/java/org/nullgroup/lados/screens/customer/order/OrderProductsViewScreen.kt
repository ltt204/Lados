package org.nullgroup.lados.screens.customer.order

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.common.ProfileTopAppBar
import org.nullgroup.lados.compose.common.TwoColsItem
import org.nullgroup.lados.data.models.Color
import org.nullgroup.lados.data.models.Image
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.models.Size
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.getActionForButtonOfOrderProduct
import org.nullgroup.lados.utilities.getStatusByName
import org.nullgroup.lados.viewmodels.customer.OrderProductsState
import org.nullgroup.lados.viewmodels.customer.OrderProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderProductsViewScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    navController: NavController? = null,
    orderProductsViewModel: OrderProductsViewModel = hiltViewModel()
) {
    val orderProducts = orderProductsViewModel.productVariantsState.collectAsState()

    Scaffold(
        modifier = modifier
            .padding(top = paddingValues.calculateTopPadding()),
        topBar = {
            ProfileTopAppBar(
                onBackClick = {
                    navController?.navigateUp()
                },
                content = "Order Products"
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (orderProducts.value) {
            is OrderProductsState.Loading -> {
                LoadOnProgress(
                    modifier = modifier.fillMaxSize(),
                    content = {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    }
                )
            }

            is OrderProductsState.Error -> {
                /*TODO: Handle Error*/
            }

            is OrderProductsState.Success -> {
                val currentOrder = orderProductsViewModel.currentOrder.collectAsState()
                Log.d("OrderProductsViewScreen", "Order Products: ${orderProducts.value}")
                Log.d("OrderProductsViewScreen", "Current Order: ${currentOrder.value}")
                OrderProductsView(
                    modifier = Modifier.padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp
                    ),
                    orderProducts = (orderProducts.value as OrderProductsState.Success).orderProducts,
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
    orderProducts: List<Pair<Product, ProductVariant>>,
    orderStatus: OrderStatus,
    navController: NavController? = null,
) {
    val buttonAction = orderStatus.getActionForButtonOfOrderProduct()

    LazyColumn(modifier = modifier) {
        items(items = orderProducts, key = { it.first.id }) { (product, variant) ->
            OrderProductItem(
                modifier = Modifier.heightIn(min = 72.dp, max = 128.dp),
                product = product,
                variant = variant,
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
    navController: NavController? = null,
    buttonAction: Pair<String?, (NavController, String?) -> Unit>
) {
    val image = variant.images.firstOrNull()?.link
    Log.d("OrderProductItem", "Image: $image")
    Card(
        modifier = modifier,
        onClick = { /*TODO*/ }) {
        TwoColsItem(
            content = {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        model = ImageRequest
                            .Builder(LocalContext.current)
                            .crossfade(true)
                            .data(image)
                            .build(),
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Crop,
                        loading = {
                            CircularProgressIndicator(modifier = Modifier.size(12.dp))
                        },
                        error = {
                            Log.d(
                                "OrderProductItem",
                                "Image failed to load: ${it.result.throwable.message}"
                            )
                            Text(text = "Image failed to load: ${it.result.throwable.message}")
                        }
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Column {
                            Text(text = "Size: ${variant.size.sizeName}", fontSize = 12.sp)
                            Text(text = "Color: ${variant.color.colorName}", fontSize = 12.sp)
                        }
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isSale = variant.salePrice != null
                            Text(
                                text = "$${variant.originalPrice}",
                                textDecoration = if (isSale) TextDecoration.LineThrough else null,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (isSale) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null
                                )
                                Text(
                                    text = "$${variant.salePrice}",
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    color = androidx.compose.ui.graphics.Color.Red,
                                    fontWeight = FontWeight.SemiBold
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
                            product.id
                        )
                    }) {
                        Text(
                            text = it,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            onClick = {
                /*TODO: Navigate to product detail*/
            }
        )
    }
}

@Preview
@Composable
fun OrderProductsViewScreenPreview() {
//    // Sample test data
//    val testOrder = Order(
//        orderId = "order123",
//        customerId = "customer@test.com",
//        orderStatusLog = mapOf(
//            OrderStatus.CREATED.name to System.currentTimeMillis() - 500000,
//            OrderStatus.CONFIRMED.name to System.currentTimeMillis() - 400000,
//            OrderStatus.SHIPPED.name to System.currentTimeMillis() - 300000,
//            OrderStatus.DELIVERED.name to System.currentTimeMillis() - 200000
//        ),
//        orderProducts = listOf(
//            OrderProduct(
//                id = "orderProduct1",
//                productId = "product1",
//                variantId = "variant1",
//                amount = 2,
//                totalPrice = 59.98
//            ),
//            OrderProduct(
//                id = "orderProduct2",
//                productId = "product2",
//                variantId = "variant2",
//                amount = 1,
//                totalPrice = 29.99
//            ),
//            OrderProduct(
//                id = "orderProduct3",
//                productId = "product3",
//                variantId = "variant3",
//                amount = 3,
//                totalPrice = 89.97
//            )
//        ),
//        orderTotal = 179.94,
//        deliveryAddress = "123 Test Street, Test City",
//        customerPhone = "1234567890"
//    )
//    val orderProducts = remember {
//        mutableStateOf<OrderProductsState>(
//            OrderProductsState.Success(
//                mutableListOf(
//                    Pair(
//                        Product(
//                            id = "product1",
//                            name = "Test Product 1",
//                            variants = listOf(
//                                ProductVariant(
//                                    id = "variant1",
//                                    size = Size(1, "Skibidi"),
//                                    color = Color(1, "Red", "#FF0000"),
//                                    originalPrice = 29.99,
//                                    salePrice = 19.99,
//                                    images = listOf(
//                                        Image(
//                                            id = "image1",
//                                            link = "https://via.placeholder.com/150"
//                                        )
//                                    )
//                                )
//                            )
//                        ) to ProductVariant(
//                            id = "variant1",
//                            size = Size(1, "Small"),
//                            color = Color(1, "Red", "#FF0000"),
//                            originalPrice = 29.99,
//                            salePrice = 19.99,
//                            images = listOf(
//                                Image(
//                                    id = "image1",
//                                    link = "https://jimmyluxury.in/products/fabric-lucas-double-clothe-cream-full-sleeve-shirt"
//                                )
//                            )
//                        )
//                    ),
//                )
//            )
//        )
//    }
//
//    val currentOrder = remember {
//        mutableStateOf(testOrder)
//    }
//
//    OrderProductsView(
//        orderProducts = orderProducts,
//        currentOrder = currentOrder,
//    )
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
        buttonAction = Pair("Action") { navController, _ -> /*TODO*/ }
    )
}