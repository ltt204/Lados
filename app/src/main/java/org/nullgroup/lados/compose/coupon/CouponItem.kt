package org.nullgroup.lados.compose.coupon

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.R
import org.nullgroup.lados.ui.theme.LadosTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class ItemState {
    NORMAL,
    SELECTED,
    DISABLED,
    INVALID
}

fun LocalDateTime.toAlternateString(format: String = "HH'h'mm dd/MM/yyyy"): String {
    return this.format(DateTimeFormatter.ofPattern(format))
}

@Composable
fun CouponItem(
    modifier: Modifier = Modifier,
    couponCode: String,
    discountPercentage: Int,
    minimumOrderAmount: Double?,
    maximumDiscount: Double?,
    expiredAt: LocalDateTime,
    onItemClicked: (() -> Unit)? = null,
    extraNote: String? = null, // only applied when [trailingArea] is null
    trailingArea : (@Composable () -> Unit)? = null,
    itemState: ItemState = ItemState.NORMAL
) {
    val containerColor = when (itemState) {
        ItemState.SELECTED -> LadosTheme.colorScheme.primaryContainer
        ItemState.INVALID -> LadosTheme.colorScheme.errorContainer
        else -> LadosTheme.colorScheme.secondaryContainer
    }
    val contentColor = when (itemState) {
        ItemState.SELECTED -> LadosTheme.colorScheme.onPrimaryContainer
        ItemState.INVALID -> LadosTheme.colorScheme.onErrorContainer
        else -> LadosTheme.colorScheme.onSecondaryContainer
    }
    val bodySmallTypo = LadosTheme.typography.bodySmall
    val bodyMediumTypo = LadosTheme.typography.bodyMedium
    val bodyLargeTypo = LadosTheme.typography.bodyLarge

    val cardColors = CardColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = containerColor.copy(alpha = 0.38f),
        disabledContentColor = contentColor,
    )

    Card (
        modifier = modifier
            .heightIn(max = 128.dp)
            .fillMaxWidth()
            .clip(LadosTheme.shape.medium)
        ,
        onClick = { onItemClicked?.invoke() },
        enabled = itemState != ItemState.DISABLED && itemState != ItemState.INVALID,
        colors = cardColors,
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(12.dp)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = couponCode,
                    style = bodyLargeTypo.copy(fontWeight = FontWeight.ExtraBold),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.coupon_discount_percentage_desc,
                        discountPercentage
                    ),
                    style = bodyMediumTypo.copy(fontWeight = FontWeight.Bold),
                )
                if (minimumOrderAmount != null || maximumDiscount != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buildAnnotatedString {
                            if (minimumOrderAmount != null) {
                                append(
                                    stringResource(
                                        R.string.coupon_minimum_order_amount_desc,
                                        minimumOrderAmount
                                    ))
                            }
                            if (minimumOrderAmount != null && maximumDiscount != null) {
                                append(", ")
                            }
                            if (maximumDiscount != null) {
                                append(
                                    stringResource(
                                        R.string.coupon_maximum_discount_desc,
                                        maximumDiscount
                                    ))
                            }
                        },
                        style = bodySmallTypo,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.coupon_expire_at_desc,
                        expiredAt.toAlternateString()
                    ),
                    style = bodySmallTypo,
                )
            }
            if (trailingArea != null || extraNote.isNullOrEmpty().not()) {
                Spacer(modifier = Modifier.width(4.dp))
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .widthIn(max = 64.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    if (trailingArea == null) {
                        Text(
                            text = extraNote!!,
                            overflow = TextOverflow.Visible,
                            style = bodySmallTypo.copy(fontWeight = FontWeight.Bold),
                        )
                    } else {
                        trailingArea()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CouponItemPreview() {
    LadosTheme {
        CouponItem(
            couponCode = "LADOS10",
            discountPercentage = 10,
            minimumOrderAmount = 100.0,
            maximumDiscount = 50.0,
            expiredAt = LocalDateTime.now().plusDays(7),
            extraNote = "Recommended",
        )
    }
}
