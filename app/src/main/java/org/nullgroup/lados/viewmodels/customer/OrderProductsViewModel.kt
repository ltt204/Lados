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
import org.nullgroup.lados.utilities.OrderStatus
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
                .collect {
                    currentOrder.value = it
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
                                    val pair = product to variant
                                    val currentList =
                                        (_productVariantsState.value as? OrderProductsState.Success)?.orderProducts
                                            ?: mutableListOf()
                                    currentList.add(pair)
                                    _productVariantsState.value = OrderProductsState.Success(
                                        orderProducts = currentList
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
    data class Success(val orderProducts: MutableList<Pair<Product, ProductVariant>>) :
        OrderProductsState()

    data class Error(val message: String) : OrderProductsState()
}