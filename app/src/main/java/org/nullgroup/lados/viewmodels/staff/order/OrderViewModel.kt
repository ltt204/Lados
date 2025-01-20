package org.nullgroup.lados.viewmodels.staff.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.repositories.interfaces.order.OrderRepository
import org.nullgroup.lados.screens.common.LoginScreen
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.viewmodels.customer.order.OrderState
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
    private var loadJob: Job? = null
    private var currentStatus: OrderStatus? = null
    private var searchJob: Job? = null

    private fun loadOrders(status: OrderStatus, isRefresh: Boolean = false) {
        loadJob?.cancel()

        if (currentStatus != status || !isRefresh) {
            orders.update {
                it.copy(
                    orders = emptyList(),
                    hasMoreOrders = true,
                    isLoading = true,
                )
            }
            currentLastDocument = null
            currentStatus = status
        }

        Log.d("OrderViewModel", status.name)
        loadJob = viewModelScope.launch {
            try {
                if (!isRefresh && !orders.value.hasMoreOrders) {
                    return@launch
                }

                Log.d("loadOrders", "load")

                orderRepository.getOrderByStatus(status, lastDocument = currentLastDocument)
                    .onSuccess { page ->
                        currentLastDocument = page.lastDocument
                        Log.d("loadOrders", (page.lastDocument != null).toString())
                        orders.update { current ->
                            current.copy(
                                orders = if (isRefresh) page.orders else (current.orders + page.orders).distinctBy { it.orderId },
                                hasMoreOrders = page.lastDocument != null && page.orders.isNotEmpty(),
                                isLoading = false,
                                error = null,
                            )
                        }
                    }
                    .onFailure {
                        orders.update {
                            it.copy(error = it.error, isLoading = false, hasMoreOrders = false)
                        }
                    }
            } catch (e: Exception) {
                orders.update {
                    it.copy(
                        error = e.message,
                        isLoading = false,
                        hasMoreOrders = false,
                    )
                }
            }
        }
    }

    private fun searchOrders(query: String) {
        if (query.isEmpty()) {
            currentStatus?.let { loadOrders(it, true) }
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            orders.update {
                it.copy(isLoading = true)
            }

            try {
                orderRepository.searchOrdersById(query)
                    .onSuccess { searchResult ->
                        orders.update {
                            it.copy(
                                orders = searchResult,
                                isLoading = false,
                                hasMoreOrders = false,
                                error = null,
                            )
                        }
                    }
                    .onFailure { error ->
                        orders.update {
                            it.copy(
                                error = error.message,
                                isLoading = false,
                                hasMoreOrders = false
                            )
                        }
                    }

            } catch (e: Exception) {
                orders.update {
                    it.copy(
                        error = e.message,
                        isLoading = false,
                        hasMoreOrders = false,
                    )
                }
            }
        }
    }

    fun handleEvent(event: OrderScreenEvent) {
        when (event) {
            is OrderScreenEvent.LoadOrders -> {
                loadOrders(event.status, event.isRefresh)
            }

            is OrderScreenEvent.SearchOrders -> {
                searchOrders(event.query)
            }
        }
    }
}