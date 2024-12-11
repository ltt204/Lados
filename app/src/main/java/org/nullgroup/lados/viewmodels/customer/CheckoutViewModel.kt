package org.nullgroup.lados.viewmodels.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.CheckoutInfo
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository
import javax.inject.Inject

enum class CheckoutError {
    FAILED_TO_GET_CHECKOUT_INFO,
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    // private val userRepository: UserRepository,
    private val cartItemRepository: CartItemRepository,
    private val orderRepository: OrderRepository
): ViewModel() {
    private val _orderingItems = MutableStateFlow<List<CartItem>>(mutableListOf())
    val orderingItems = _orderingItems.asStateFlow()

    private val _orderingItemInformation =
        MutableStateFlow<Map<String, Pair<Product?, ProductVariant?>>>(mutableMapOf())
    val orderingItemInformation = _orderingItemInformation.asStateFlow()

    private val _checkoutInfo = MutableStateFlow<CheckoutInfo?>(null)
    val checkoutInfo = _checkoutInfo.asStateFlow()

    private val _failureRaiser = MutableStateFlow<CheckoutError?>(null)
    val failureRaiser = _failureRaiser.asStateFlow()

    // TODO: Hardcoded customer ID
    private val customerId = "admin@test.com"

    init {
        viewModelScope.launch {
            val checkoutInfoGetResult = cartItemRepository.getCheckoutInfo(customerId)
            _checkoutInfo.value = checkoutInfoGetResult.getOrNull()

            if (checkoutInfoGetResult.isFailure) {
                _failureRaiser.value = CheckoutError.FAILED_TO_GET_CHECKOUT_INFO
                return@launch
            }

            cartItemRepository.getCheckingOutItemsAsFlow(customerId).collect {
                if (it.isNotEmpty()) {
                    _orderingItems.value = _orderingItems.value.plus(
                        it.map { it.cartItem }
                    )
                    _orderingItemInformation.value = _orderingItemInformation.value.plus(
                        it.map {
                            it.cartItem.id to Pair(it.product, it.variant)
                        }
                    )
                }
            }
        }
    }

    val checkoutHandler: (
        onCheckoutFailure: ((reason: String) -> Unit)?,
        onSuccessfulCheckout: (() -> Unit)?
    ) -> (() -> Unit) = { onCheckoutFailure, onSuccessfulCheckout ->
        {
            viewModelScope.launch {
                onCheckout(onCheckoutFailure, onSuccessfulCheckout)
            }
        }
    }

    /// Should only be triggered if the CheckoutDetail is already shown
    private suspend fun onCheckout(
        onCheckoutFailure: ((reason: String) -> Unit)? = null,
        onSuccessfulCheckout: (() -> Unit)? = null
    ) {
        val orderProductList = orderingItems.value.map { cartItem ->
            val productVariant = orderingItemInformation.value[cartItem.id]?.second
            if (productVariant != null) {
                OrderProduct(
                    productId = cartItem.productId,
                    variantId = cartItem.variantId,
                    amount = cartItem.amount,
                    totalPrice = (productVariant.salePrice ?: productVariant.originalPrice) * cartItem.amount
                )
            } else {
                null
            }
        }.mapNotNull { it }

        val checkoutDetail = checkoutInfo.value!!
        val newOrder = Order(
            customerId = customerId,
            orderProducts = orderProductList,
            orderTotal = checkoutDetail.total
        )

        val result = viewModelScope.async {
            // TODO: Combine stock reduction and order creation
            orderRepository.createOrder(
                customerId = customerId,
                order = newOrder,
            )
        }.await()

        if (result.isFailure) {
            onCheckoutFailure?.invoke("Can't create order for the current items:" +
                    " ${result.exceptionOrNull()?.message}")
            return
        }

        withContext(Dispatchers.IO) {
            launch {
                cartItemRepository.removeCartItemsFromCart(
                    customerId = customerId,
                    cartItemIds = orderingItems.value.map { it.id }
                )
            }
            launch {
                cartItemRepository.clearCheckingOutItems(
                    customerId = customerId,
                )
            }
            launch {
                cartItemRepository.clearCheckoutInfo(
                    customerId = customerId,
                )
            }
        }

        onSuccessfulCheckout?.invoke()
    }
}