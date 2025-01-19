package org.nullgroup.lados.viewmodels.admin

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductNameAndCategory
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.order.OrderRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import org.nullgroup.lados.utilities.OrderStatus
import org.nullgroup.lados.utilities.getDay
import org.nullgroup.lados.utilities.getDayString
import org.nullgroup.lados.utilities.getMonth
import org.nullgroup.lados.utilities.getMonthString
import org.nullgroup.lados.utilities.getYear
import org.nullgroup.lados.utilities.toUsdCurrency
import org.nullgroup.lados.viewmodels.customer.home.CategoryUiState
import org.nullgroup.lados.viewmodels.customer.home.ProductUiState
import org.nullgroup.lados.viewmodels.customer.order.OrderProductsState
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val products: StateFlow<List<Product>> = productRepository.getProductsFlow()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _ordersUIState = MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)
    val ordersUIState = _ordersUIState.asStateFlow()

    private val _originalOrders = MutableStateFlow<List<Order>>(emptyList())

    private  val _listProducts = MutableStateFlow<List<OrderProduct>>(emptyList())
    val listProducts: StateFlow<List<OrderProduct>> = _listProducts.asStateFlow()
    private  val _listOrders = MutableStateFlow<List<Order>>(emptyList())
    val listOrders: StateFlow<List<Order>> = _listOrders.asStateFlow()

    private fun fetchOrders(){
        viewModelScope.launch {
            try {
                val getOrdersResult = orderRepository.getAllOrdersFromFirestore()
                _ordersUIState.value = OrdersUiState.Success(getOrdersResult.getOrNull() ?: emptyList())
                _originalOrders.value= getOrdersResult.getOrNull() ?: emptyList()
            } catch (e: Exception) {
                _ordersUIState.value = OrdersUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun filterOrders(startDate: Date, endDate: Date) {
        val currentState = _ordersUIState.value
        if (currentState is OrdersUiState.Success) {
            _ordersUIState.value = OrdersUiState.Success(
                orders = currentState.orders.filter { order ->
                    val creationTimestamp = order.orderStatusLog[OrderStatus.CREATED.name]
                    val creationDate: Date? = creationTimestamp?.let { Date(it) }
                    creationDate != null && creationDate >= startDate && creationDate <= endDate
                }
            )
        }
    }

    private val _categoryUiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryUiState = _categoryUiState.asStateFlow()

    private val _productUiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val productUiState = _productUiState.asStateFlow()

    private val _originalProducts = MutableStateFlow<List<Product>>(emptyList())

    init {
        fetchCategories()
        fetchProducts()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val getCategoriesResult = categoryRepository.getAllCategoriesFromFireStore()
                _categoryUiState.value =
                    CategoryUiState.Success(getCategoriesResult.getOrNull() ?: emptyList())
            } catch (e: Exception) {
                Log.d("HomeViewModel", "fetchCategories: ${e.message}")
                _categoryUiState.value = CategoryUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val getProductsResult = productRepository.getAllProductsWithNameAndCategoryFromFireStore()
                val products = getProductsResult.getOrNull() ?: emptyList()
                _originalProducts.value = products
                _productUiState.value = ProductUiState.Success(products)
            } catch (e: Exception) {
                Log.d("HomeViewModel", "fetchProducts: ${e.message}")
                _productUiState.value = ProductUiState.Error(e.message ?: "An error occurred")
            }
        }
    }


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

    val revenueByMonth: MutableStateFlow<DashBoardRevenueState> =
        MutableStateFlow(DashBoardRevenueState.Loading)
    val revenueByDay: MutableStateFlow<DashBoardRevenueState> =
        MutableStateFlow(DashBoardRevenueState.Loading)


    init {
        getRevenueByMonth(Calendar.getInstance().get(Calendar.YEAR).toString())
        getRevenueByDay(Calendar.getInstance().get(Calendar.MONTH).toString(), Calendar.getInstance().get(Calendar.YEAR).toString())
        fetchOrders()
    }

    fun getRevenueByDay(fromMonth: String, fromYear: String){
        viewModelScope.launch {
            orderRepository.getOrdersForAdmin()
                .flowOn(Dispatchers.IO)
                .catch {
                    revenueByDay.value = DashBoardRevenueState.Error(it.message ?: "An error occurred")
                }
                .collect{
                    val _revenueByDay = it
                        .filter { order ->
                            order.orderStatusLog.entries.minBy { it.value }.value.getMonth().toString() == fromMonth
                                    && order.orderStatusLog.entries.minBy { it.value }.value.getYear().toString() == fromYear
                        }
                        .groupBy { order ->
                            val day =
                                order.orderStatusLog.entries.minBy { it.value }.value.getDayString()
                            Log.d("Revenue", "Day: $day")
                            day
                        }
                        .mapValues {
                            it.value.filter {
                                it.orderStatusLog.entries.firstOrNull { it.key == OrderStatus.RETURNED.name } == null
                                        && it.orderStatusLog.entries.firstOrNull { it.key == OrderStatus.CANCELLED.name } == null
                            }.sumOf { it.orderTotal.toUsdCurrency() }
                        }
                    revenueByDay.value = DashBoardRevenueState.Success(_revenueByDay)
                }

        }
    }


    fun getRevenueByMonth(fromYear: String) {
        viewModelScope.launch {
            orderRepository.getOrdersForAdmin()
                .flowOn(Dispatchers.IO)
                .catch {
                    revenueByMonth.value = DashBoardRevenueState.Error(it.message ?: "An error occurred")
                }
                .collect {
                    Log.d("Revenue", it.map { it.orderTotal.toUsdCurrency() }.toString())
                    val _revenueByMonth = it
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
                    //Log.d("Revenue", revenueByMonth.toString())
                    //Log.d("Revenue", "List revenue: ${revenueByMonth.values}")
                    revenueByMonth.value = DashBoardRevenueState.Success(_revenueByMonth)
                }
        }
    }
}

sealed class DashBoardRevenueState {
    data object Loading : DashBoardRevenueState()
    data class Success(val data: Map<String, Double>) : DashBoardRevenueState()
    data class Error(val message: String) : DashBoardRevenueState()
}

sealed class DashBoardProductsState {
    data object Loading : DashBoardProductsState()
    data class Success(val data: Map<String, Product>) : DashBoardProductsState()
    data class Error(val message: String) : DashBoardProductsState()
}

sealed class OrdersUiState {
    data object Loading : OrdersUiState()
    data class Success(val orders: List<Order>) : OrdersUiState()
    data class Error(val message: String) : OrdersUiState()
}