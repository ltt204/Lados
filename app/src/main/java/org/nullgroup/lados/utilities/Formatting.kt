package org.nullgroup.lados.utilities

import java.util.Locale

fun Number?.ToUSDCurrency(): String {
    if (this == null) {
        return "$0.00"
    }
    return "$${String.format(Locale.US ,"%,.2f", this)}"
}