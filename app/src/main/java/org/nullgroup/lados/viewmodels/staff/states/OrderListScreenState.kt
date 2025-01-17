package org.nullgroup.lados.viewmodels.staff.states

import org.nullgroup.lados.data.models.Order

data class OrderListScreenState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val hasMoreOrders: Boolean = false,
    val error: String? = null,
)