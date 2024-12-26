package org.nullgroup.lados.utilities

import java.util.Locale

// Enum for order status
enum class OrderStatus {
    CREATED,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    // Optional feature: Customer can cancel the order
    RETURNED,
    // Optional feature: Customer can return the products after delivery
    // if they are not satisfied
    // Maybe unused
}

enum class UserRole {
    CUSTOMER,
    STAFF,
    ADMIN
}

enum class SupportedRegion(val locale: Locale) {
    US(locale = Locale("en", "United States")),
    VIETNAM(locale = Locale("vi", "Vietnam"))
}