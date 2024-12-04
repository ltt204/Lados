package org.nullgroup.lados.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nullgroup.lados.data.models.Cart
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.Order
import org.nullgroup.lados.data.models.OrderProduct
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository
import org.nullgroup.lados.data.repositories.interfaces.ProductRepository
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    // private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
    private val orderRepository: OrderRepository,
): ViewModel() {

    private var _cart: Cart? = null
    val cart: Cart? get() = _cart

    private val _cartItems = MutableStateFlow<List<CartItem>>(mutableListOf())
    val cartItems = _cartItems.asStateFlow()

    private val _cartItemInformation =
        MutableStateFlow<Map<String, Pair<Product?, ProductVariant?>>>(mutableMapOf())
    val cartItemInformation = _cartItemInformation.asStateFlow()

    // If an item whose id is in this map,
    // it means the amount of that item has been changed while interacting with the CartScreen
    // but it doesn't mean that the amount in the map is different than the one currently in _cartItems
    private val _originalAmount: MutableMap<String, Int> = mutableMapOf()

    private val _selectedCartItemIds = MutableStateFlow<Set<String>>(mutableSetOf())
    val selectedCartItems = _selectedCartItemIds.combine(cartItems) { selectedIds, items ->
        selectedIds.mapNotNull { selectedId ->
            items.find { it.id == selectedId }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    val isAnyCartItemSelected = { _selectedCartItemIds.value.isNotEmpty() }

    // TODO: Hardcode
    private val userEmail = "customer@test.com"

    var orderDiscountRate: Double = 0.0
    var orderMaximumDiscount: Double = 0.0
    val checkoutDetail: () -> CheckoutInfo = {
        val selectedItems = selectedCartItems.value

        var subtotal = 0.0 // Before apply sale in each product
        var total = 0.0 // After apply sale in each product, before apply order discount
        selectedItems.forEach { cartItem ->
            val productVariant = cartItemInformation.value[cartItem.id]?.second
            if (productVariant != null) {
                subtotal += productVariant.originalPrice * cartItem.amount
                total += productVariant.salePrice * cartItem.amount
            }
        }
        val productDiscount = subtotal - total
        val orderDiscount = (total * orderDiscountRate).coerceAtMost(orderMaximumDiscount)
        CheckoutInfo(
            subtotal,
            productDiscount,
            orderDiscount,
            total - orderDiscount
        )
    }

    init {
        viewModelScope.launch {
            refreshCartInformation()
        }
    }

    // This one is rather for errors
    var onRefreshComplete: ((String) -> Unit)? = null
    suspend fun refreshCartInformation() {
        _cart = viewModelScope.async {
            val initResult = cartItemRepository.getOrInitializeCart(userEmail)

            if (initResult.isFailure || initResult.getOrNull() == null) {
                // Handle error
                withContext(Dispatchers.Main) {
                    // Show error message
                    Log.e(
                        "CartViewModel",
                        "Error initializing cart: ",
                        initResult.exceptionOrNull()
                    )
                    return@withContext
                }
                return@async null
            }
            return@async initResult.getOrNull()
        }.await()

        if (cart != null) {
            // TODO: to be removed
            viewModelScope.async {
                addCartItem("1", "v1", 3)
                addCartItem("1", "v2", 2)
                addCartItem("2", "v4", 1)
                addCartItem("2", "v6", 1)
                addCartItem("3", "v9", 5)
            }.await()

            viewModelScope.launch {
                cartItemRepository.getCartItemsAsFlow(cart!!.customerId)
                    .onCompletion { cause ->
                        if (cause == null) {
                            onRefreshComplete?.invoke("Cart refreshed")
                        } else {
                            onRefreshComplete?.invoke("Error refreshing cart: ${cause.message}")
                        }
                    }
                    .collect { cartItems ->
                        val newItems = cartItems.filter {
                            cartItem -> _cartItems.value.none { it.id == cartItem.id }
                        }.toMutableList()
                        newItems.forEach { newItem ->
                            _originalAmount.remove(newItem.id)
                        }
                        _cartItems.value = _cartItems.value.map { oldItem ->
                            // Replacing old items with new items of the same id
                            newItems.firstOrNull { it.id == oldItem.id }?.also {
                                newItems.remove(it)
                            } ?: oldItem
                        }
                        _cartItems.value = _cartItems.value.plus(newItems)
                    }
            }

            viewModelScope.launch {
                _cartItems
                    .filterNotNull()
                    .collect { cartItems ->
                        getItemsInformation(cartItems)
                    }
            }
        } else {
            onRefreshComplete?.invoke("Error initializing cart")
        }
    }
    private fun getItemsInformation(cartItems: List<CartItem>) {
        for (cartItem in cartItems) {
            viewModelScope.launch {
                val correspondingProductResult =
                    productRepository.getProductByIdFromFireStore(cartItem.productId)
                if (correspondingProductResult.isFailure) {
                    Log.e(
                        "CartViewModel",
                        "Error getting product ${cartItem.productId} variant: ",
                        correspondingProductResult.exceptionOrNull()
                    )
                    return@launch
                }
                val product = correspondingProductResult.getOrNull()
                if (product == null) {
                    _cartItemInformation.value = _cartItemInformation.value.plus(
                        cartItem.id to Pair(null, null)
                    )
                    return@launch
                }
                var productVariant = product.variants.find { it.id == cartItem.variantId }
                _cartItemInformation.value = _cartItemInformation.value.plus(
                    cartItem.id to Pair(product, productVariant)
                )
            }
        }

        // Currently the getProductAndVariant function is not working

//        // Maybe quicker, but required special access to Products without using ProductRepository
//        for (cartItem in cartItems) {
//            viewModelScope.launch {
//                val cartItemInfo = cartItemRepository.getProductAndVariant(
//                        productId = cartItem.productId,
//                        variantId = cartItem.variantId
//                )
//                if (cartItemInfo.isFailure) {
//                    Log.e(
//                        "CartViewModel",
//                        "Error getting product ${cartItem.productId} variant: ",
//                        cartItemInfo.exceptionOrNull()
//                    )
//                    return@launch
//                } else {
//                    _cartItemInformation.value = _cartItemInformation.value.plus(
//                        cartItem.id to cartItemInfo.getOrNull()!!
//                    )
//                }
//            }
//        }
    }

    // TODO: Remove this function
    // Use this when adding product to cart in other places
    suspend fun addCartItem(productId: String, variantId: String, amount: Int): Result<Boolean> {
        if (cart == null) {
            return Result.failure(Exception("Cart not found"))
        }

        val cartItem = CartItem(
            productId = productId,
            variantId = variantId,
            amount = amount
        )

        return cartItemRepository.addCartItemToCart(
            cartId = cart!!.customerId,
            cartItem = cartItem
        )

    }

    fun updateCartItemAmountLocally(cartItemId: String, amountDelta: Int) {
        if (cart == null || amountDelta == 0) {
            return
        }

        _cartItems.value.find { it.id == cartItemId }?.let { originalItem ->
            if (!_originalAmount.containsKey(originalItem.id)) {
                _originalAmount[originalItem.id] = originalItem.amount
            }
            val newAmount = originalItem.amount + amountDelta
            val itemIndex = _cartItems.value.indexOf(originalItem)
            _cartItems.value =
                _cartItems.value.subList(0, itemIndex) +
                originalItem.copy(amount = newAmount.coerceAtLeast(0)) +
                _cartItems.value.subList(itemIndex + 1, _cartItems.value.size)
        }
    }

    fun removeSelectedCartItemLocally() {
        if (cart == null) {
            return
        }

        selectedCartItems.value.forEach { cartItem ->
            if (!_originalAmount.containsKey(cartItem.id)) {
                _originalAmount[cartItem.id] = cartItem.amount
            }

            _cartItems.value = _cartItems.value.minus(cartItem)
            _cartItems.value = _cartItems.value.plus(cartItem.copy(amount = 0))

            _selectedCartItemIds.value = _selectedCartItemIds.value.minus(cartItem.id)
        }
    }

    fun onCartItemSelectionChanged(cartItemId: String, isSelected: Boolean) {
        if (cart == null) {
            return
        }

        _cartItems.value.find { it.id == cartItemId }?.let {
            if (isSelected) {
                _selectedCartItemIds.value = _selectedCartItemIds.value.plus(it.id)
            } else {
                _selectedCartItemIds.value = _selectedCartItemIds.value.minus(it.id)
            }
        }
    }

    suspend fun commitChangesToDatabase(): Result<Boolean> {
        if (cart == null) {
            return Result.failure(Exception("Cart not found"))
        }

        val result = cartItemRepository.updateCartItemsAmount(
            cartId = cart!!.customerId,
            adjustmentInfo = _cartItems.value
                .filter { it.amount != _originalAmount[it.id] }
                .map { it.id to it.amount }
        )
        if (result.isFailure) {
            // Handle error
            Log.e(
                "CartViewModel",
                "Error updating cart items: ",
                result.exceptionOrNull()
            )
            return Result.failure(
                Exception("Error updating cart items: ${result.exceptionOrNull()?.message}")
            )
        }
        _originalAmount.clear()
        return Result.success(true)
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

    private suspend fun onCheckout(
        onCheckoutFailure: ((reason: String) -> Unit)? = null,
        onSuccessfulCheckout: (() -> Unit)? = null
        ) {
        if (cart == null) {
            return
        }

        val selectedItems = selectedCartItems.value

        if (selectedItems.isEmpty()) {
            return
        }

        // No need to save now
//        val savingResult = viewModelScope.async {
//            commitChangesToDatabaseAsync()
//        }.await()
//
//        if (savingResult.isFailure) {
//            onCheckoutFailure?.invoke(
//                "Can't save changes to database: ${savingResult.exceptionOrNull()?.message}"
//            )
//            return
//        }

        val orderProductList = selectedItems.map { cartItem ->
            val productVariant = cartItemInformation.value[cartItem.id]?.second
            if (productVariant != null) {
                OrderProduct(
                    productId = cartItem.productId,
                    variantId = cartItem.variantId,
                    amount = cartItem.amount,
                    totalPrice = productVariant.salePrice * cartItem.amount
                )
            } else {
                null
            }
        }.mapNotNull { it }

        val checkoutDetail = checkoutDetail()
        val newOrder = Order(
            customerId = cart!!.customerId,
            orderProducts = orderProductList,
            orderTotal = checkoutDetail.total
        )

        val result = viewModelScope.async {
            orderRepository.createOrder(
                customerId = cart!!.customerId,
                order = newOrder,
            )
        }.await()

        if (result.isFailure) {
            onCheckoutFailure?.invoke("Can't create order for the current items:" +
                    " ${result.exceptionOrNull()?.message}")
            return
        }

        removeSelectedCartItemLocally()
        viewModelScope.launch {
            commitChangesToDatabase()
        }
        onSuccessfulCheckout?.invoke()
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.launch {
            commitChangesToDatabase()
        }
    }
}

data class CheckoutInfo (
    val subtotal: Double,
    val productDiscount: Double,
    val orderDiscount: Double,
    val total: Double,
)