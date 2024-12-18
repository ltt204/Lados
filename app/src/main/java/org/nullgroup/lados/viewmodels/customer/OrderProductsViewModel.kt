package org.nullgroup.lados.viewmodels.customer

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
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

    var currentOrder: MutableStateFlow<Order> = MutableStateFlow(Order())
        private set

    private var _productVariantsState =
        MutableStateFlow<OrderProductsState>(OrderProductsState.Loading)
    val productVariantsState: StateFlow<OrderProductsState> =
        this._productVariantsState.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            orderRepository
                .getOrderById(orderId)
                .flowOn(Dispatchers.IO)
                .collect { order ->
                    currentOrder.value = order
                    _orderProducts.emit(order.orderProducts)
                    val currentList = mutableListOf<Pair<Product, ProductVariant>>()
                    order.orderProducts.forEach { orderProduct ->
                        try {
                            val data =
                                productRepository.getProductByIdFromFireStore(orderProduct.productId)
                                    .getOrNull() ?: throw Exception("Product not found")
                            Log.d(
                                "OrderProductsViewModel",
                                "ProductId: ${orderProduct.productId} and data $data"
                            )
                            currentList.add(
                                Pair(
                                    data,
                                    data.variants.first { it.id == orderProduct.variantId })
                            )
                        } catch (e: Exception) {
                            _productVariantsState.value =
                                OrderProductsState.Error(e.message ?: "Failed to fetch products")
                        }
                    }
                    Log.d("OrderProductsViewModel", "After fetching products")
                    _productVariantsState.value = OrderProductsState.Success(currentList)
                }
        }
    }
}


sealed class OrderProductsState {
    data object Loading : OrderProductsState()
    data class Success(val orderProducts: MutableList<Pair<Product, ProductVariant>>) :
        OrderProductsState()

    data class Error(val message: String) : OrderProductsState()
}