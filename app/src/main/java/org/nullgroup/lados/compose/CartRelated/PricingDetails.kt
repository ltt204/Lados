package org.nullgroup.lados.compose.cartRelated

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PricingDetails(
    subtotal: String,
    productDiscount: String,
    orderDiscount: String? = null,
    total: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Subtotal
        PricingRow(label = "Subtotal", value = subtotal)
        Spacer(modifier = Modifier.height(8.dp))

        // Discount
        PricingRow(label = "Discount", value = productDiscount)
        Spacer(modifier = Modifier.height(8.dp))

        if (orderDiscount != null && orderDiscount.isNotEmpty()) {
            PricingRow(label = "Order Discount", value = orderDiscount)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Total
        PricingRow(label = "Total", value = total, isBold = true)
    }
}

@Composable
fun PricingRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val defaultLabelStyle = MaterialTheme.typography.bodyLarge.copy(
            color = Color.Gray,
        )
        val defaultValueStyle = MaterialTheme.typography.bodyLarge.copy(
            color = Color.Black,
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