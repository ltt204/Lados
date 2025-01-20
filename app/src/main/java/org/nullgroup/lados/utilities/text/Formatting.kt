package org.nullgroup.lados.utilities.text

import java.util.Locale
import kotlin.math.pow

const val DOUBLE_EQUALITY_DELTA = 1e-9
const val CURRENCY_PRECISION = 2u

fun Double?.toTextFieldString(
    precision: UInt = CURRENCY_PRECISION,
    locale: Locale = Locale.getDefault()
): String {
    return this?.let {
        if (it < DOUBLE_EQUALITY_DELTA) "0"
        else if (it - it.toInt() < DOUBLE_EQUALITY_DELTA) String.format(
            locale,
            "%.0f",
            it
        )
        else String.format(
            locale,
            "%.${precision}f",
            it
        )
    } ?: ""
}

fun String.fromTextFieldStringAsNullableDouble(
    onPointReserved: (Boolean) -> Unit = {}
): Double? {
    val trimmedString = this.trim()

    if (trimmedString.isEmpty()) {
        return null
    }

//    if (trimmedString.matches(Regex("^[0-9]+\\.[0-9]*$"))) {
//        val parts = trimmedString.split(".")
//        return parts[0].toDouble()
//    }
//
//    return trimmedString.toDoubleOrNull()

    return trimmedString.fromTextFieldStringAsDouble(onPointReserved)
}

fun Double.toTextFieldString(
    precision: UInt = CURRENCY_PRECISION,
    locale: Locale = Locale.getDefault()
): String {
    return this.let {
        if (it < DOUBLE_EQUALITY_DELTA) ""
        else if (it - it.toInt() < DOUBLE_EQUALITY_DELTA) String.format(
            locale,
            "%.0f",
            it
        )
        else String.format(
            locale,
            "%.${precision}f",
            it
        )
    }
}

fun String.fromTextFieldStringAsDouble(
    onPointReserved: (Boolean) -> Unit = {}
): Double {
    val trimmedString = this.trim()

    if (trimmedString.isEmpty()) {
        return 0.0
    }

    val parts = trimmedString.split(".")
    val numberPart = if (parts.isNotEmpty()) parts[0].let {
        if (it.isEmpty()) 0 else it.toIntOrNull()
    } else 0
    val decimalPart = if (parts.size > 1) parts[1].let {
        if (it.isEmpty()) 0.also { onPointReserved(true) } else it.toIntOrNull()
    } else 0

    if (numberPart == null || decimalPart == null) {
        return Double.NaN
    }

    return numberPart.toDouble() +
            if (parts.size > 1) decimalPart.toDouble() / 10.0.pow(parts[1].length) else 0.0
}

