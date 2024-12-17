package org.nullgroup.lados.viewmodels.customer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nullgroup.lados.data.models.Address
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.CheckoutInfo
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository
import org.nullgroup.lados.data.repositories.interfaces.IUserAddressRepository
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Inject

enum class CheckoutError {
    FAILED_TO_GET_CHECKOUT_INFO,
}

data class InsufficientOrderProductInfo(
    val productName: String,
    val productSize: String,
    val productColor: String,
    val orderedAmount: Int,
    val availableStock: Int,
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userAddressRepository: IUserAddressRepository,
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

    // Map cartItem's id to its corresponding product and variant
    private val _insufficientOrderItems = MutableStateFlow<List<InsufficientOrderProductInfo>>(emptyList())
    val insufficientOrderItems = _insufficientOrderItems.asStateFlow()

    // TODO: Resolve the case when the view-model can't get the cached checkout info
    //      Triggered when the checking-out screen is not launched from the cart screen
    private val _checkoutFailure = MutableStateFlow<CheckoutError?>(null)
    val checkoutFailure = _checkoutFailure.asStateFlow()

    // TODO: Hardcoded customer ID
    private lateinit var customerId: String

    private lateinit var _userAddresses: StateFlow<List<Address>>
    val userAddresses: StateFlow<List<Address>>
        get() = _userAddresses

    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress = _selectedAddress.asStateFlow()

    private val _userPhoneNumber = mutableStateOf("???")
    val userPhoneNumber: State<String> = _userPhoneNumber

    init {
        viewModelScope.launch {
            // TODO: Return the actual user data
//            val currentUser = userRepository.getCurrentUser()
//            customerId = currentUser.id
            customerId = "admin@test.com"
//            _userPhoneNumber.value = currentUser.phoneNumber
            _userPhoneNumber.value = "1234567890"

            viewModelScope.launch {
                _userAddresses = userAddressRepository.getAddressesFlow()
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(),
                        initialValue = emptyList()
                    )
            }

            viewModelScope.launch {
                val checkoutInfoGetResult = cartItemRepository.getCheckoutInfo(customerId)
                _checkoutInfo.value = checkoutInfoGetResult.getOrNull()

                if (checkoutInfoGetResult.isFailure) {
                    _checkoutFailure.value = CheckoutError.FAILED_TO_GET_CHECKOUT_INFO
//                    return@launch
                } else {
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
        }
    }

    val onAddressChanged = { address: Address ->
        _selectedAddress.value = address
    }

    val checkoutHandler: (
        onCheckoutFailure: ((reason: String?) -> Unit)?,
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
        onCheckoutFailure: ((reason: String?) -> Unit)? = null,
        onSuccessfulCheckout: (() -> Unit)? = null
    ) {
        if (selectedAddress.value == null) {
            onCheckoutFailure?.invoke("Please select an address")
            return
        }

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
            orderTotal = checkoutDetail.total,
            deliveryAddress = selectedAddress.value!!.toString(),
            customerPhoneNumber = userPhoneNumber.value,
        )

        val result = viewModelScope.async {
            orderRepository.createOrder(
                customerId = customerId,
                order = newOrder,
            )
        }.await()

        if (result.isFailure) {
            onCheckoutFailure?.invoke("Can't create the order at the moment:" +
                    " ${result.exceptionOrNull()?.message}")
            return
        }

        val (isOrderCreated, invalidOrderingItems) = result.getOrNull()!!

        if (!isOrderCreated && invalidOrderingItems.isEmpty()) {
            // This route should not be reached
            onCheckoutFailure?.invoke("Can't create the order at the moment!")
            return
        } else if (!isOrderCreated) {
            onCheckoutFailure?.invoke(null)
            _insufficientOrderItems.value = invalidOrderingItems.mapNotNull { entry ->
                try { // Extra defense against null pointer exception; although it should not happen
                    val orderProduct = entry.key
                    val cartItem = orderingItems.value.find {
                        it.productId == orderProduct.productId
                                && it.variantId == orderProduct.variantId
                    }!!
                    val product = orderingItemInformation.value[cartItem.id]?.first!!
                    val productVariant = orderingItemInformation.value[cartItem.id]?.second!!
                    InsufficientOrderProductInfo(
                        productName = product.name,
                        productSize = productVariant.size.sizeName,
                        productColor = productVariant.color.colorName,
                        orderedAmount = entry.key.amount,
                        availableStock = entry.value
                    )
                } catch (_: Exception) {
                    null
                }
            }
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

    val clearInsufficientOrderItems = {
        _insufficientOrderItems.value = emptyList()
    }
}