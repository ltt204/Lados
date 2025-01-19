package org.nullgroup.lados.utilities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Undo
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Locale

// Enum for order status
enum class OrderStatus {
    CREATED,
    // Optional feature: Customer can cancel the order
    CANCELLED,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    RETURNED;
    // Optional feature: Customer can return the products after delivery
    // if they are not satisfied
    // Maybe unused

    fun getIcon(): ImageVector {
        return when (this) {
            CREATED -> Icons.Default.Add
            CONFIRMED -> Icons.Default.Check
            SHIPPED -> Icons.Default.LocalShipping
            DELIVERED -> Icons.Default.Done
            CANCELLED -> Icons.Default.Close
            RETURNED -> Icons.Default.Undo
        }
    }
}

enum class UserRole {
    CUSTOMER,
    STAFF,
    ADMIN
}

enum class SupportedRegion(val locale: Locale, val regionName: String) {
    US(locale = Locale("en", "US"), regionName = "United States"),
    VIETNAM(locale = Locale("vi", "VN"), regionName = "Vietnam")
}