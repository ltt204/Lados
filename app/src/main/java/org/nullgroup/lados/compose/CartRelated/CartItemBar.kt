package org.nullgroup.lados.compose.CartRelated

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun CartItemBar(
    imageUrl: String,
    title: String,
    originalPrice: String,
    salePrice: String,
    size: String,
    color: String,
    onAddClick: (() -> Unit)? = null,
    quantity: Int = 0,
    onRemoveClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
//            .background(Color.White, shape = RoundedCornerShape(8.dp)),
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
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))

            val specialStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            val annotatedString = buildAnnotatedString {
                append("Size - ")
                withStyle(specialStyle) {
                    append(size)
                }
                append("   Color - ")
                withStyle(specialStyle) {
                    append(color)
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Price and Actions
        Column(horizontalAlignment = Alignment.End) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                if (originalPrice != salePrice) {
                    Text(
                        text = originalPrice,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = "Change to sale price",
                        tint = Color(0xFF9371FF)
                    )
                }
                Text(
                    text = salePrice,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
            }


            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.End),
            ) {
                if (onRemoveClick != null) {
                    IconButton(onClick = onRemoveClick) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Remove",
                            tint = Color(0xFF9371FF)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }


                if (onRemoveClick == null && onAddClick == null) {
                    Text(
                        text = "x$quantity",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }




                if (onAddClick != null) {
                    Spacer(modifier = Modifier.width(8.dp))
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
}