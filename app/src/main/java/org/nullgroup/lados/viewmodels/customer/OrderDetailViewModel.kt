package org.nullgroup.lados.viewmodels.customer

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository
import org.nullgroup.lados.screens.Screen
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val orderId =
        checkNotNull(savedStateHandle.get<String>(Screen.Customer.Order.OrderDetail.ID_ARG))

    private var _orderDetailState = MutableStateFlow<OrderDetailState>(OrderDetailState.Loading)
    val orderDetailState: StateFlow<OrderDetailState> = _orderDetailState.asStateFlow()

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            orderRepository.getOrderById(orderId)
                .flowOn(Dispatchers.IO)
                .catch {
                    _orderDetailState.value =
                        OrderDetailState.Error(it.message ?: "An error occurred")
                }
                .collect {
                    Log.d("OrderDetailViewModel", "Order: $it")
                    _orderDetailState.value = OrderDetailState.Success(it)
                }
        }
    }
}

sealed class OrderDetailState {
    data object Loading : OrderDetailState()
    data class Success(val currentOrder: Order) : OrderDetailState()
    data class Error(val message: String) : OrderDetailState()
}
