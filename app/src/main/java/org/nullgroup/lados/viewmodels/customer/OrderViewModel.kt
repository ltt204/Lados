package org.nullgroup.lados.viewmodels.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository
import org.nullgroup.lados.utilities.OrderStatus
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    private var _orderState = MutableStateFlow<OrderState>(OrderState.Loading)
    val orderState: StateFlow<OrderState> = _orderState.asStateFlow()

    private lateinit var orders: MutableStateFlow<List<Order>>

    init {
        fetch()
    }

    private fun fetch() {
        viewModelScope.launch {
            orderRepository.getOrders()
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    _orderState.value = OrderState.Error(e.message ?: "An error occurred")
                }
                .collect {
                    orders = MutableStateFlow(it)
                    Log.d("OrderViewModel", "Filtering orders by status: ${orders.value}")
                    _orderState.value = OrderState.Success(
                        it.filter { order ->
                            order.orderStatusLog.entries.last().key == OrderStatus.CREATED.name
                        }
                    )
                }
        }
    }

    fun filterOrderByStatus(status: OrderStatus) {
        viewModelScope.launch {
            val filteredOrders = orders.value.filter {
                it.orderStatusLog.entries.last().key == status.name
            }
            Log.d("OrderViewModel", "Filtering orders by status $status: $filteredOrders")
            _orderState.update { OrderState.Success(filteredOrders) }
        }
    }
}

sealed class OrderState {
    data object Loading : OrderState()
    data class Success(val orders: List<Order>) : OrderState()
    data class Error(val message: String) : OrderState()
}