package org.nullgroup.lados.screens.customer.product

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.Timestamp
import org.nullgroup.lados.R
import org.nullgroup.lados.data.models.Image
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.models.Size
import org.nullgroup.lados.data.models.UserEngagement
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.order.OrderItemsArea
import org.nullgroup.lados.utilities.getCurrentUTCFormattedTime
import org.nullgroup.lados.viewmodels.customer.ReviewProductViewModel
import org.nullgroup.lados.viewmodels.customer.ReviewProductsState


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReviewProductScreen(
    productId: String,
    variantId: String,
    viewModel: ReviewProductViewModel = hiltViewModel(),
    navController: NavController
) {

    var ratings by remember {
        mutableIntStateOf(1)
    }
    var reviews by remember {
        mutableStateOf("")
    }

    val uiState = viewModel.productVariantsState.collectAsState()
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.background(Color.White),
        topBar = {
            TopBar(
                leadingIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ProductTheme.backgroundColor)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                },
                title = "Rating and Review",
                trailingIcon = {
                    Spacer(modifier = Modifier.size(24.dp))
                }
            )

        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.sendReview(
                        productId = productId,
                        engagement = UserEngagement(
                            productId = productId,
                            userId = "u5",
                            ratings = ratings,
                            reviews = reviews,
                            createdAt = Timestamp.now()
                        )
                    )
                    Toast.makeText(context, "Review sent!", Toast.LENGTH_SHORT).show()
                    navController.navigate("${Screen.Customer.ProductDetailScreen.route}/$productId")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProductTheme.primaryColor,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Send",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(
                        vertical = 10.dp,
                        horizontal = 16.dp
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                when (uiState.value) {
                    is ReviewProductsState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight()
                            ,
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator()
                        }
                    }

                    is ReviewProductsState.Success -> {
                        val currentProduct = (uiState.value as ReviewProductsState.Success).product

                        ProductSection(
                            productName = currentProduct.keys.first().name,
                            variant = currentProduct.values.first()
                        )
                    }

                    is ReviewProductsState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight()
                            ,
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator()
                        }
                    }
                }

                RatingSection(
                    onRatings = {
                        ratings = it
                    }
                )
                ReviewSection(
                    onReview = {
                        reviews = it
                    }
                )
            }
        }
    }

}

@Composable
fun TopBar(
    leadingIcon: @Composable (() -> Unit)? = null,
    title: String = "",
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .padding(top = 24.dp)
    ) {
        leadingIcon?.invoke()

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )

        trailingIcon?.invoke()

    }
}

@Composable
fun ProductSection(
    productName: String,
    variant: ProductVariant
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(variant.images.first().link)
                .crossfade(true)
                .build(),
            contentDescription = "Product Image",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.FillBounds
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = productName,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                text = "Size: ${variant.size.sizeName}",
                fontSize = 14.sp
            )

            Text(
                text = "Color: ${variant.color.colorName}",
                fontSize = 14.sp
            )
        }

    }

}

@Composable
fun RatingSection(
    onRatings: (Int) -> Unit = {}
) {

    val list = listOf(
        "Terrible",
        "Bad",
        "Okay",
        "Good",
        "Excellent"
    )

    var ratings by remember {
        mutableIntStateOf(1)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = "Ratings",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),

            ) {
            repeat(5) { index ->
                Icon(
                    painter = painterResource(id = R.drawable.star_icon),
                    contentDescription = "Star",
                    tint = if (index < ratings) ProductTheme.primaryColor else Color.LightGray,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            ratings = index + 1;
                            onRatings(ratings)
                        }
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = list[ratings - 1],
                color = ProductTheme.primaryColor,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )

        }
    }
}

@Composable
fun ReviewSection(
    onReview: (String) -> Unit = {}
) {

    var reviews by remember {
        mutableStateOf("")
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Reviews",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )

        TextField(
            value = reviews,
            onValueChange = {
                reviews = it
                onReview(it)
            },
            textStyle = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            ),
            placeholder = {
                Text(
                    text = "Enter your review here",
                    color = Color.Gray,
                    fontSize = 18.sp
                )

            },

            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                .heightIn(min = 200.dp),

            maxLines = Int.MAX_VALUE,
            shape = RoundedCornerShape(10.dp)
        )

    }

}


