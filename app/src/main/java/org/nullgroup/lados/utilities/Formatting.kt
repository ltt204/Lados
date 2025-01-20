package org.nullgroup.lados.utilities

import java.text.NumberFormat
import java.util.Locale

fun Number?.toCurrency(locale: Locale = Locale.getDefault()): String {
    if (this == null) {
        return NumberFormat.getCurrencyInstance(locale).format(0)
    }

    val currentLocale = Locale.getDefault()
    val amount = this.toDouble()
    if (currentLocale.language == "vi") {
        return NumberFormat.getCurrencyInstance(locale).format(amount)
    }

    return NumberFormat.getCurrencyInstance(locale).format(amount/23000)
}