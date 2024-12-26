package org.nullgroup.lados.utilities

import android.content.Context
import org.nullgroup.lados.R
import java.util.Locale

fun Number?.toCurrency(context: Context): String {
    if (this == null) {
        return context.getString(R.string.product_price, "0.00")
    }
    val formatted = String.format(Locale.US, "%.2f", this)
    return context.getString(R.string.product_price, formatted)
}