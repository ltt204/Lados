package org.nullgroup.lados.viewmodels.staff.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.repositories.interfaces.order.OrderRepository
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.viewmodels.staff.events.OrderScreenEvent
import org.nullgroup.lados.viewmodels.staff.states.OrderListScreenState
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    var orders = MutableStateFlow(OrderListScreenState())
        private set
    private var currentLastDocument: DocumentSnapshot? = null

    private fun loadOrders(status: OrderStatus, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                orders.update { it.copy(orders = emptyList()) }
                currentLastDocument = null
            }

            orders.update { it.copy(isLoading = true) }

            orderRepository.getOrderByStatus(status, lastDocument = currentLastDocument)
                .catch { error ->
                    orders.update { it.copy(error = error.message, isLoading = false) }
                }
                .collect { page ->
                    currentLastDocument = page.lastDocument
                    orders.update { current ->
                        current.copy(
                            orders = if (isRefresh) page.orders else current.orders + page.orders,
                            hasMoreOrders = page.lastDocument != null,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
        }
    }

    fun handleEvent(event: OrderScreenEvent) {
        when (event) {
            is OrderScreenEvent.UpdateOrderStatus -> {
                updateOrderStatus(event.orderId, event.newStatus)
            }

            is OrderScreenEvent.LoadOrders -> {
                loadOrders(event.status, event.isRefresh)
            }
        }
    }

    private fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            orders.update {
                it.copy(isLoading = true)
            }
            orderRepository.updateOrderStatus(orderId, newStatus)
                .onSuccess {
                    orders.update { it.copy(error = null, isLoading = false) }
                }
                .onFailure { error ->
                    orders.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
}