package org.nullgroup.lados.viewmodels.staff.order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.order.OrderRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.screens.Screen
import javax.inject.Inject

@HiltViewModel
class OrderProductsViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val orderId = checkNotNull(
        savedStateHandle.get<String>(Screen.Staff.OrderProducts.ID_ARG)
    )

    var currentOrder: MutableStateFlow<Order> = MutableStateFlow(Order())
        private set

    var productVariantState = MutableStateFlow<OrderProductsState>(OrderProductsState.Loading)
        private set

    var orderProducts = MutableStateFlow<List<OrderProduct>>(emptyList())
        private set

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            orderRepository.getOrderByIdForStaff(orderId)
                .flowOn(Dispatchers.IO)
                .collect { order ->
                    currentOrder.value = order
                    orderProducts.emit(order.orderProducts)

                    val currentList = mutableListOf<Pair<Product, ProductVariant>>()

                    order.orderProducts.forEach { orderProduct ->
                        try {
                            val data =
                                productRepository.getProductByIdFromFireStore(orderProduct.productId)
                                    .getOrNull() ?: throw Exception("Product not found")

                            currentList.add(
                                Pair(
                                    data,
                                    data.variants.first() { it.id == orderProduct.variantId }
                                )
                            )
                        } catch (e: Exception) {
                            productVariantState.value = OrderProductsState.Error(e.message)
                        }
                    }

                    productVariantState.value = OrderProductsState.Success(currentList)
                }
        }
    }
}

sealed class OrderProductsState {
    data object Loading : OrderProductsState()
    data class Success(val products: MutableList<Pair<Product, ProductVariant>>) :
        OrderProductsState()

    data class Error(val message: String?) : OrderProductsState()
}