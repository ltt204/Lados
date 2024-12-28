package org.nullgroup.lados.utilities

import java.text.NumberFormat
import java.util.Locale

fun Number?.toCurrency(locale: Locale = Locale.getDefault()): String {
    if (this == null) {
        return NumberFormat.getCurrencyInstance(locale).format(0)
    }
    return NumberFormat.getCurrencyInstance(locale).format(this)
}