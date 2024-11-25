package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import org.nullgroup.lados.viewmodels.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    modifier: Modifier = Modifier
) {
    val cartViewModel: CartViewModel = hiltViewModel()

    val cartItems = cartViewModel.cartItems.collectAsStateWithLifecycle()
    val cartItemInformation = cartViewModel.cartItemInformation.collectAsStateWithLifecycle()

    val hardcodedCartId = "0"
    val defaultImageUrl = "https://placehold.co/600x400"
    val defaultTitle = "Unknown Product"
    val defaultValue = "???"
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text("Cart")
                }
            )
        },
        floatingActionButton = {
            OutlinedButton(
                onClick = {
                    cartViewModel.getCartItems(hardcodedCartId)
                    cartViewModel.getItemsInformation()
                },
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text("Moment of truth")
            }

        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
//                items(cartItems.value) { cartItem ->
//                    CartItem(
//                        modifier = Modifier
//                            .padding(innerPadding)
//                            .fillMaxWidth()
//                    )
//                }
//                items(10) {
//                    CartItem(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(start = innerPadding)
//                    )
//                }
                items(cartItems.value) { cartItem ->
                    val (product, productVariant) =
                        cartItemInformation.value[cartItem.id] ?: Pair(null, null)
                    CartItem(
                        imageUrl = productVariant?.images?.firstOrNull()?.imageLink ?: defaultImageUrl,
                        title = product?.name ?: defaultTitle,
                        price = (productVariant?.salePrice ?: defaultValue).toString(),
                        size = productVariant?.size?.sizeName ?: defaultValue,
                        color = productVariant?.color?.colorName ?: defaultValue,
                        onAddClick = { /* TODO */ },
                        onRemoveClick = { /* TODO */ },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                }
            }
        }

    )
}

@Composable
fun CartItem(
    imageUrl: String,
    title: String,
    price: String,
    size: String,
    color: String,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image Section
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Product Details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = title,
                color = Color.Black,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Size - $size   Color - $color",
                style = MaterialTheme.typography.body2.copy(color = Color.Gray)
            )
        }

        // Price and Actions
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = price,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onRemoveClick) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Remove",
                        tint = Color(0xFF9371FF)
                    )
                }
                IconButton(onClick = onAddClick) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Add",
                        tint = Color(0xFF9371FF)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    CartScreen()
}
