package org.nullgroup.lados.compose.cart

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.TwoColsItem
import org.nullgroup.lados.screens.customer.cart.ItemState
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun CartItemBar(
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
    modifier: Modifier = Modifier
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

    TwoColsItem(
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = textColor
        ),
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
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = LadosTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Column {
                        Text(
                            text = "Size: $size",
                            style = LadosTheme.typography.bodySmall
                        )
                        Text(
                            text = "Color: $color",
                            style = LadosTheme.typography.bodySmall
                        )
                    }

                    Column(
                        modifier = Modifier,
                    ) {
                        val isSale = salePrice != null
                        Log.d("CartItemBar", "originalPrice: $originalPrice, salePrice: $salePrice, isSale: $isSale")
                        Text(
                            text = originalPrice,
                            textDecoration = if (isSale) TextDecoration.LineThrough else null,
                            style = LadosTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        )
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
                    }
                }
            }
        },
        trailingAction = {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onRemoveClick != null) {
                    FilledIconButton(
                        onClick = onRemoveClick,
                        enabled = clickEnabled,
                        colors = iconButtonColors,
                        modifier = Modifier.scale(0.75f)
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
                        modifier = Modifier.scale(0.75f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_add),
                            contentDescription = "Remove",
                        )
                    }
                }

            }
        },
        onClick = {
            onItemSelected()
        }
    )
}
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .padding(end = 4.dp)
//        ) {
//            Text(
//                text = title,
//                style = bodyMediumTypo.copy(fontWeight = FontWeight.Bold),
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            val boldSpanStyle = bodySmallTypo.toSpanStyle().copy(
//                fontWeight = FontWeight.Bold
//            )
//            val normalSpanStyle = bodySmallTypo.toSpanStyle()
//            Text(
//                text = buildAnnotatedString {
//                    withStyle(normalSpanStyle) {
//                        append(stringResource(R.string.cart_item_size))
//                    }
//                    withStyle(boldSpanStyle) {
//                        append(size)
//                    }
//                },
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//            Text(
//                text = buildAnnotatedString {
//                    withStyle(normalSpanStyle) {
//                        append(stringResource(R.string.cart_item_color))
//                    }
//                    withStyle(boldSpanStyle) {
//                        append(color)
//                    }
//                },
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//            Column(
//                verticalArrangement = Arrangement.spacedBy(4.dp),
////                horizontalArrangement = Arrangement.spacedBy(4.dp),
////                modifier = Modifier.align(Alignment.End)
//            ) {
//                if (originalPrice != salePrice) {
//                    Text(
//                        text = originalPrice,
//                        style = bodyMediumTypo.copy(
//                            textDecoration = TextDecoration.LineThrough,
//                            fontWeight = FontWeight.Bold,
//                        )
//                    )
//
//                }
//                Text(
//                    text = salePrice,
//                    style = bodyMediumTypo.copy(fontWeight = FontWeight.Bold),
//                )
//            }
//        }

// Price and Actions
//    Column(
//        horizontalAlignment = Alignment.End,
//        modifier = Modifier.wrapContentWidth(),
//    ) {
//
////            Row {
////                Text(
////                    text = buildAnnotatedString {
////                        append("x")
////                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
////                            append(quantity.toString())
////                        }
////                    },
////                    style = bodyLargeTypo,
////                )
////            }
//
////            val iconButtonColors = IconButtonColors(
////                contentColor = Color.White,
////                containerColor = Color(0xFF9371FF),
////                disabledContentColor = Color.Gray,
////                disabledContainerColor = Color(0xFF9371FF).copy(alpha = 0.25f)
////            )


