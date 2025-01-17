package org.nullgroup.lados.compose.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.R
import org.nullgroup.lados.screens.customer.cart.ItemState
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun CartItemBar(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    originalPrice: String,
    salePrice: String?,
    size: String,
    color: String,
    clickEnabled: Boolean = true,
    onAddClick: (() -> Unit)? = null,
    onItemSelected: (() -> Unit) = {},
    quantity: Int = 0,
    onRemoveClick: (() -> Unit)? = null,
    itemState: ItemState? = ItemState.UNSELECTED,
) {
    if (quantity == 0) {
        return
    }

    val buttonColor = when (itemState) {
        ItemState.INVALID -> LadosTheme.colorScheme.error
        else -> LadosTheme.colorScheme.secondary
    }
    val buttonTextColor = when (itemState) {
        ItemState.INVALID -> LadosTheme.colorScheme.onError
        else -> LadosTheme.colorScheme.onSecondary
    }
    val textColor = when (itemState) {
        ItemState.SELECTED -> LadosTheme.colorScheme.onPrimaryContainer
        ItemState.INVALID -> LadosTheme.colorScheme.onErrorContainer
        else -> LadosTheme.colorScheme.onSecondaryContainer
    }
    val containerColor = when (itemState) {
        ItemState.SELECTED -> LadosTheme.colorScheme.primaryContainer
        ItemState.INVALID -> LadosTheme.colorScheme.errorContainer
        else -> LadosTheme.colorScheme.secondaryContainer
    }
    val bodySmallTypo = LadosTheme.typography.bodySmall.copy(color = textColor)
    val bodyMediumTypo = LadosTheme.typography.bodyMedium.copy(color = textColor)
    val bodyLargeTypo = LadosTheme.typography.bodyLarge.copy(color = textColor)

    val iconButtonColors = IconButtonColors(
        contentColor = buttonTextColor,
        containerColor = buttonColor,
        disabledContentColor = buttonTextColor,
        disabledContainerColor = buttonColor.copy(alpha = 0.38f)
    )

    // Product Details

    Card(
        modifier = Modifier
            .heightIn(max = 128.dp)
            .fillMaxWidth()
            .clip(LadosTheme.shape.medium),
        onClick = {
            onItemSelected()
        },
        colors = CardDefaults.cardColors(
            containerColor = LadosTheme.colorScheme.surfaceContainerHigh,
            contentColor = LadosTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = LadosTheme.colorScheme.outline,
        )
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .size(100.dp)
                    .clip(LadosTheme.shape.medium),
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .crossfade(true)
                    .data(imageUrl)
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
            Column {
                Text(
                    text = title,
                    style = LadosTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(modifier = Modifier.height(LadosTheme.size.small))
                Row {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.cart_item_size, size),
                                style = LadosTheme.typography.bodySmall
                            )
                            Text(
                                text = stringResource(id = R.string.cart_item_color, color),
                                style = LadosTheme.typography.bodySmall
                            )
                        }
                        Column(
                            modifier = Modifier,
                        ) {
                            val isSale = salePrice != null
                            if (salePrice != null) {
                                Text(
                                    text = salePrice,
                                    style = LadosTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = LadosTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    ),
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = originalPrice,
                                textDecoration = if (isSale) TextDecoration.LineThrough else null,
                                style = LadosTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = if (isSale) 14.sp else 16.sp,
                                ),
                            )
                        }

                    }
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (onRemoveClick != null) {
                            FilledIconButton(
                                onClick = onRemoveClick,
                                enabled = clickEnabled,
                                colors = iconButtonColors,
                                modifier = Modifier
                                    .scale(0.75f)
                                    .size(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_remove),
                                    contentDescription = "Add",
                                )
                            }
                        }
                        if (onAddClick == null) {
                            Text(
                                text = stringResource(id = R.string.order_items_header, quantity),
                                style = bodyLargeTypo
                            )
                        } else {
                            Text(
                                text = quantity.toString(),
                                style = bodyLargeTypo
                            )
                        }
                        if (onAddClick != null) {
                            // Spacer(modifier = Modifier.width(4.dp))
                            FilledIconButton(
                                onClick = onAddClick,
                                enabled = clickEnabled,
                                colors = iconButtonColors,
                                modifier = Modifier
                                    .scale(0.75f)
                                    .size(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_add),
                                    contentDescription = "Remove",
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CartItemPreview() {
    LadosTheme {
        CartItemBar(
            imageUrl = "https://via.placeholder.com/150",
            title = "Long Product Title For Testing",
            originalPrice = "$100",
            salePrice = "$80",
            size = "M",
            color = "Red",
            quantity = 1,
            onAddClick = {},
            onRemoveClick = {}
        )
    }
}

@Preview
@Composable
fun CheckoutItemPreview() {
    LadosTheme {
        CartItemBar(
            imageUrl = "https://via.placeholder.com/150",
            title = "Long Product Title For Testing",
            originalPrice = "$100",
            salePrice = "$80",
            size = "M",
            color = "Red",
            quantity = 12,
        )
    }
}