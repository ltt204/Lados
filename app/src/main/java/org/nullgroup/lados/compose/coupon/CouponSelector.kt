package org.nullgroup.lados.compose.coupon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.R
import org.nullgroup.lados.data.models.CustomerCoupon
import org.nullgroup.lados.utilities.datetime.currentHostTimeZoneInString
import org.nullgroup.lados.utilities.datetime.toLocalDateTime
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun CouponSelector(
    modifier: Modifier = Modifier,
    currentCoupon: CustomerCoupon?,
    onClicked: () -> Unit
) {
    val currentTimeZone = currentHostTimeZoneInString()
    if (currentCoupon != null) {
        CouponItem(
            modifier = modifier
                .fillMaxWidth(),
            couponCode = currentCoupon.code,
            discountPercentage = currentCoupon.discountPercentage,
            minimumOrderAmount = currentCoupon.minimumOrderAmount,
            maximumDiscount = currentCoupon.maximumDiscount,
            itemState = ItemState.SELECTED,
            expiredAt = currentCoupon.expiredAt.toLocalDateTime(currentTimeZone),
            onItemClicked = onClicked
        )
    } else {
        val cardColors = CardColors(
            containerColor = LadosTheme.colorScheme.surfaceContainerLowest,
            contentColor = LadosTheme.colorScheme.onSurface,
            disabledContainerColor = LadosTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.38f),
            disabledContentColor = LadosTheme.colorScheme.onSurface,
        )
        val bodyLargeTypo = LadosTheme.typography.bodyLarge

        val onSecondaryColor = LadosTheme.colorScheme.onSurface
        val cornerRadius = LadosTheme.size.medium

        Card(
            modifier = modifier
                .height(64.dp)
                .fillMaxWidth()
                .clip(LadosTheme.shape.medium)
                .drawBehind {
                    // Draw a dashed border
                    drawRoundRect(
                        color = onSecondaryColor,
                        style = Stroke(
                            width = 4f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
                        ),
                        cornerRadius = CornerRadius(
                            cornerRadius.toPx()
                        )
                    )
                },
            onClick = { onClicked() },
            colors = cardColors,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.coupon_selector_select_desc),
                    style = bodyLargeTypo.copy(fontWeight = FontWeight.ExtraBold),
                )
            }
        }
    }
}