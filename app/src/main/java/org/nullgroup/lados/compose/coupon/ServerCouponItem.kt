package org.nullgroup.lados.compose.coupon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import org.nullgroup.lados.utilities.toCurrency
import java.time.LocalDateTime
import kotlin.time.Duration

@Composable
fun ServerCouponItem(
    modifier: Modifier = Modifier,
    couponCode: String = "",
    discountPercentage: Int = 0,
    minimumOrderAmount: Double? = null,
    maximumDiscount: Double? = null,
    startDate: LocalDateTime = LocalDateTime.now(),
    endDate: LocalDateTime = LocalDateTime.now(),
    usageDuration: Duration? = null,
    redeemedCount: Int = 0,
    maximumRedemption: Int? = null,
    autoFetching: Boolean = false,
    enabled: Boolean = true,
    onItemClicked: (() -> Unit)? = null,
    extraNote: String? = null, // only applied when [trailingArea] is null
    trailingArea: (@Composable () -> Unit)? = null,
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

    Card(
        modifier = modifier
            .heightIn(max = 128.dp)
            .fillMaxWidth()
            .clip(LadosTheme.shape.medium),
        onClick = { onItemClicked?.invoke() },
        enabled = enabled,
        colors = cardColors,
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = couponCode + if (autoFetching) " \uD83C\uDF10" else "",
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
                                        minimumOrderAmount.toCurrency()
                                    )
                                )
                            }
                            if (minimumOrderAmount != null && maximumDiscount != null) {
                                append(", ")
                            }
                            if (maximumDiscount != null) {
                                append(
                                    stringResource(
                                        R.string.coupon_maximum_discount_desc,
                                        maximumDiscount.toCurrency()
                                    )
                                )
                            }
                        },
                        style = bodySmallTypo,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        append(
                            stringResource(
                                R.string.coupon_start_date_desc,
                                startDate.toAlternateString()
                            )
                        )
                        append(", ")
                        append(
                            stringResource(
                                R.string.coupon_end_date_desc,
                                endDate.toAlternateString()
                            )
                        )
                    },
                    style = bodySmallTypo,
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (usageDuration != null) {
                    val dayPart = usageDuration.inWholeDays
                    val hourPart = usageDuration.inWholeHours % 24
                    val minutePart = usageDuration.inWholeMinutes % 60
                    Text(
                        text = buildAnnotatedString {
                            append("\u231B")
                            if (dayPart > 0) {
                                append("$dayPart ${stringResource(R.string.coupon_usage_duration_day)}")
                            }
                            if (hourPart > 0) {
                                append("$hourPart ${stringResource(R.string.coupon_usage_duration_hour)}")
                            }
                            if (minutePart > 0) {
                                append("$minutePart ${stringResource(R.string.coupon_usage_duration_minute)}")
                            }
                        },
                        style = bodySmallTypo,
                    )
                }

                Text(
                    text =
                    stringResource(
                        R.string.coupon_redeemed_count_with_max_redeem_desc,
                        redeemedCount,
                        maximumRedemption ?: "\u221E"
                    ),
                    style = bodyLargeTypo.copy(fontWeight = FontWeight.Bold),
                )

                if (trailingArea != null || extraNote.isNullOrEmpty().not()) {
                    if (trailingArea == null) {
                        Text(
                            text = extraNote!!,
                            modifier = Modifier.padding(vertical = 2.dp),
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
fun ServerCouponItemPreview() {
    LadosTheme {
        ServerCouponItem(
            couponCode = "NEWLADOSER",
            discountPercentage = 10,
            minimumOrderAmount = 100.0,
            maximumDiscount = 50.0,
            startDate = LocalDateTime.of(2025, 1, 1, 0, 0),
            endDate = LocalDateTime.of(2025, 2, 1, 0, 0),
            usageDuration = Duration.parseIsoString("D1DT1H30M"),
            redeemedCount = 0,
            maximumRedemption = null,
            autoFetching = true
        )
    }
}