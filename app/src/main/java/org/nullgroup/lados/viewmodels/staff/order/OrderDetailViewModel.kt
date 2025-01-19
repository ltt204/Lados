package org.nullgroup.lados.viewmodels.staff.order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.repositories.interfaces.order.OrderRepository
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.utilities.OrderStatus
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val orderId = checkNotNull(
        savedStateHandle.get<String>(Screen.Staff.OrderDetail.ID_ARG)
    )

    var uiState = MutableStateFlow<OrderDetailState>(OrderDetailState.Loading)
        private set

    init {
        fetchOrder()
    }

    private fun fetchOrder() {
        viewModelScope.launch {
            orderRepository.getOrderByIdForStaff(orderId)
                .catch { error ->
                    uiState.value = OrderDetailState.Error(error.message)
                }
                .collect {
                    uiState.value = OrderDetailState.Success(it)
                }
        }
    }

    private fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, newStatus)
        }
    }

    fun handleEvent(event: OrderDetailEvent) {
        when (event) {
            is OrderDetailEvent.UpdateOrderStatus -> {
                updateOrderStatus(event.orderId, event.newStatus)
            }
        }
    }
}

sealed class OrderDetailEvent {
    data class UpdateOrderStatus(val orderId: String, val newStatus: OrderStatus) :
        OrderDetailEvent()
}

sealed class OrderDetailState {
    data object Loading : OrderDetailState()
    data class Success(val order: Order) : OrderDetailState()
    data class Error(val message: String?) : OrderDetailState()
}