package org.nullgroup.lados.compose.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.R
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun PricingDetails(
    subtotal: String,
    productDiscount: String,
    orderDiscount: String? = null,
    total: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
//        .background(LadosTheme.colorScheme.surfaceContainerLow)
    ) {
        // Subtotal
        PricingRow(
            label = stringResource(R.string.cart_subtotal),
            subtotal
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Discount
        PricingRow(
            label = stringResource(R.string.cart_discount),
            productDiscount
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (!orderDiscount.isNullOrEmpty()) {
            PricingRow(
                label = stringResource(R.string.cart_order_discount),
                orderDiscount
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Total
        PricingRow(label = stringResource(R.string.cart_total), value = total, isBold = true)
    }
}

@Composable
fun PricingRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val defaultLabelStyle = LadosTheme.typography.bodyLarge.copy(
            color = LadosTheme.colorScheme.onSurface,
        )
        val defaultValueStyle = LadosTheme.typography.bodyLarge.copy(
            color = LadosTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style =
            if (isBold) defaultLabelStyle.copy(fontWeight = FontWeight.Bold)
            else defaultLabelStyle
        )
        Text(
            text = value,
            style =
            if (isBold) defaultValueStyle.copy(fontWeight = FontWeight.Bold)
            else defaultValueStyle
        )
    }
}