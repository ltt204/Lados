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
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository
import org.nullgroup.lados.data.repositories.interfaces.ProductRepository
import org.nullgroup.lados.screens.Screen
import javax.inject.Inject

@HiltViewModel
class OrderProductsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    private val orderId =
        checkNotNull(savedStateHandle.get<String>(Screen.Customer.Order.OrderDetail.ID_ARG))
    private val _currentOrder: StateFlow<Order> = orderRepository.getOrderById(orderId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Order(),
        )

    var orderStatus = _currentOrder.value.orderStatusLog.entries.first().key

    private var _productVariantsState =
        MutableStateFlow<OrderProductsState>(OrderProductsState.Loading)
    val productVariantsState: StateFlow<OrderProductsState> =
        this._productVariantsState.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            orderRepository.getOrderById(orderId)
                .flowOn(Dispatchers.IO)
                .collect {

                    orderStatus = it.orderStatusLog.entries.first().key

                    it.orderProducts.forEach { orderProduct ->
                        val productId = orderProduct.productId
                        val variantId = orderProduct.variantId
                        productRepository.getProductByIdFlow(productId)
                            .flowOn(Dispatchers.IO)
                            .catch {
                                Log.d("OrderProductsViewModel", "Error fetching product: $it")
                                _productVariantsState.value =
                                    OrderProductsState.Error(it.message ?: "An error occurred")
                            }
                            .collect { product ->
                                Log.d("OrderProductsViewModel", "Fetched product: $product")
                                val variant = product?.variants?.find { it.id == variantId }
                                Log.d(
                                    "OrderProductsViewModel",
                                    "Fetched product: $product with variant: $variant"
                                )
                                variant?.let {
                                    _productVariantsState.value = OrderProductsState.Success(
                                        mapOf(product to variant)
                                    )
                                }
                            }
                    }
                }
        }
    }
}


sealed class OrderProductsState {
    data object Loading : OrderProductsState()
    data class Success(val orderProducts: Map<Product, ProductVariant>) : OrderProductsState()
    data class Error(val message: String) : OrderProductsState()
}