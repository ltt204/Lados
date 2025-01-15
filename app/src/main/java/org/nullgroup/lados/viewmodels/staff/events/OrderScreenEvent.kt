package org.nullgroup.lados.viewmodels.staff.events

import org.nullgroup.lados.utilities.OrderStatus

sealed class OrderScreenEvent {
    data class UpdateOrderStatus(
        val orderId: String,
        val newStatus: OrderStatus,
    ) : OrderScreenEvent()

    data class LoadOrders(
        val status: OrderStatus,
        val isRefresh: Boolean = false,
    ) : OrderScreenEvent()
}