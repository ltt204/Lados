package org.nullgroup.lados.viewmodels.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.repositories.interfaces.order.OrderRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.getMonthString
import org.nullgroup.lados.utilities.getYear
import org.nullgroup.lados.utilities.toUsdCurrency
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    val products: StateFlow<List<Product>> = productRepository.getProductsFlow()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

//    val users: StateFlow<List<User>> = flowOf(userRepository.getAllUsersFromFirestore())
//        .stateIn(
//            viewModelScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = emptyList()
//        )

//    val orders: StateFlow<List<Order>> = orderRepository.getOrders()
//        .stateIn(
//            viewModelScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = emptyList()
//        )

    val revenue: MutableStateFlow<DashBoardRevenueState> =
        MutableStateFlow(DashBoardRevenueState.Loading)

    init {
        getRevenueByMonth(Calendar.getInstance().get(Calendar.YEAR).toString())
    }

    fun getRevenueByMonth(fromYear: String) {
        viewModelScope.launch {
            orderRepository.getOrdersForAdmin()
                .flowOn(Dispatchers.IO)
                .catch {
                    revenue.value = DashBoardRevenueState.Error(it.message ?: "An error occurred")
                }
                .collect {
                    Log.d("Revenue", it.map { it.orderTotal.toUsdCurrency() }.toString())
                    val revenueByMonth = it
                        .filter { order ->
                            order.orderStatusLog.entries.minBy { it.value }.value.getYear()
                                .toString() == fromYear
                        }
                        .groupBy { order ->
                            val month =
                                order.orderStatusLog.entries.minBy { it.value }.value.getMonthString()
                            Log.d("Revenue", "Month: $month")

                            month
                        }
                        .mapValues {
                            it.value.filter {
                                it.orderStatusLog.entries.firstOrNull { it.key == OrderStatus.RETURNED.name } == null
                                        && it.orderStatusLog.entries.firstOrNull { it.key == OrderStatus.CANCELLED.name } == null
                            }.sumOf { it.orderTotal.toUsdCurrency() }
                        }
                    Log.d("Revenue", revenueByMonth.toString())
                    Log.d("Revenue", "List revenue: ${revenueByMonth.values}")
                    revenue.value = DashBoardRevenueState.Success(revenueByMonth)
                }
        }
    }
}

sealed class DashBoardRevenueState {
    data object Loading : DashBoardRevenueState()
    data class Success(val data: Map<String, Double>) : DashBoardRevenueState()
    data class Error(val message: String) : DashBoardRevenueState()
}