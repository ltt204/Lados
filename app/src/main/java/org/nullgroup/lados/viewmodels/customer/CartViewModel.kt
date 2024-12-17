package org.nullgroup.lados.viewmodels.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.CheckingOutItem
import org.nullgroup.lados.data.models.CheckoutInfo
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository
import org.nullgroup.lados.data.repositories.interfaces.ProductRepository
import javax.inject.Inject

// TODO: SCREEN STATE

@HiltViewModel
class CartViewModel @Inject constructor(
    // private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
): ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(mutableListOf())
    val cartItems = _cartItems.asStateFlow()

    private val _cartItemInformation =
        MutableStateFlow<Map<String, Pair<Product?, ProductVariant?>>>(mutableMapOf())
    val cartItemInformation = _cartItemInformation.asStateFlow()

    // If an item whose id is in this map,
    // it means the amount of that item has been changed while interacting with the CartScreen
    // but it doesn't mean that the amount in the map is different than the one currently in _cartItems
    private val _originalAmount: MutableMap<String, Int> = mutableMapOf()

    // The ids here are the ones that USED TO be selected
    // Please do not use its length to check if any item is selected
    private val _selectedCartItemIds = MutableStateFlow<Set<String>>(mutableSetOf())
    // The selected items here are the ones that is valid to be put to checkout
    val selectedCartItems = _selectedCartItemIds.combine(cartItems) { selectedIds, items ->
        selectedIds.mapNotNull { selectedId ->
            items.find {
                it.id == selectedId && it.amount > 0
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )
    val validSelectedItems = selectedCartItems.map { items ->
        items.filter {
            _cartItemInformation.value[it.id]?.first != null
                    && _cartItemInformation.value[it.id]?.second != null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    // val isAnyCartItemSelected = { _selectedCartItemIds.value.isNotEmpty() }

    // TODO: Hardcode
    private val customerId = "admin@test.com"

    var orderDiscountRate: Double = 0.0
    var orderMaximumDiscount: Double = 0.0
    val checkoutDetail: () -> CheckoutInfo = {
        val selectedItems = validSelectedItems.value

        var subtotal = 0.0 // Before apply sale in each product
        var total = 0.0 // After apply sale in each product, before apply order discount
        selectedItems.forEach { cartItem ->
            val productVariant = cartItemInformation.value[cartItem.id]?.second
            if (productVariant != null) {
                subtotal += productVariant.originalPrice * cartItem.amount
                total += (productVariant.salePrice ?: productVariant.originalPrice) * cartItem.amount
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

    private var _dataRefreshed = false
    private var _firstInitialization = true
    // This one is rather for errors
    var onRefreshError: ((String) -> Unit)? = null
    suspend fun refreshCartInformation() {
        // TODO: to be removed
        viewModelScope.async {
            addCartItem("BKj3h1PBk1YbIPy2mnOr", "RmQEs0aelFVq1OaDwhjK", 3)
            addCartItem("BKj3h1PBk1YbIPy2mnOr", "TCwBMf9PKHmfryUfmEgL", 2)
            addCartItem("Uv9JE2EwULVB6Gsjq5p7", "1aXlLfhkTo3gDZ0yFXbD", 150)
            addCartItem("bKenEU3vDCwjjKjsMapv", "0zPcXb6MbfszcEswWz4s", 500)
            addCartItem("bKenEU3vDCwjjKjsMapv", "WhatIsThisSheet", 6)
            addCartItem("Ola-la", "IDon-tKnowWhatThisHolyGrailIs", 9)
        }.await()

        viewModelScope.launch {
            cartItemRepository.getCartItemsAsFlow(customerId)
                .onCompletion { cause ->
                    if (cause == null) {
                        // This route will only be called when the flow is unsubscribed
                        // onRefreshError?.invoke("Cart refreshed")
                    } else {
                        onRefreshError?.invoke("Error refreshing cart: ${cause.message}")
                    }
                }
                .collect { cartItems ->
//                    val newItems = cartItems.filter {
//                            cartItem -> _cartItems.value.none { it.id == cartItem.id }
//                    }.toMutableList()
//                    newItems.forEach { newItem ->
//                        _originalAmount.remove(newItem.id)
//                    }
//                    _cartItems.value = _cartItems.value.map { oldItem ->
//                        // Replacing old items with new items of the same id
//                        newItems.firstOrNull { it.id == oldItem.id }?.also {
//                            newItems.remove(it)
//                        } ?: oldItem
//                    }
//                    _cartItems.value = _cartItems.value.plus(newItems)

                    _dataRefreshed = true
                    if (_firstInitialization) {
                        _cartItems.value = cartItems
                        _isLoading.value = false
                        _firstInitialization = false
                        return@collect
                    }

                    // Assume that the value collected is the final items in cart
                    // Assume that the only changes allowed with items with the same id is amount count
                    val newItems = cartItems.toMutableList()
                    _cartItems.value = _cartItems.value.mapNotNull {
                        val matchingItem = cartItems.find { item -> item.id == it.id }
                        if (matchingItem == null) {
                            // Just being cautious
                            _originalAmount.remove(it.id)

                            // _selectedCartItemIds.value = _selectedCartItemIds.value.minus(it.id)
                            null
                        } else if (it.amount != matchingItem.amount) {
                            // Just being cautious
                            _originalAmount.remove(it.id)

                            // _selectedCartItemIds.value = _selectedCartItemIds.value.minus(it.id)
                            newItems.remove(matchingItem)
                            matchingItem
                        } else {
                            newItems.remove(matchingItem)
                            it
                        }
                    }.plus(newItems)
                }
        }

        viewModelScope.launch {
            _cartItems
                .filterNotNull()
                .collect { cartItems ->
                    if (_dataRefreshed) {
                        _dataRefreshed = false
                        getItemsInformation(cartItems)
                    }
                }
        }
    }
    private fun getItemsInformation(cartItems: List<CartItem>) {
        for (cartItem in cartItems) {
            if (cartItem.amount == 0) {
                continue
            }
            if (_cartItemInformation.value.containsKey(cartItem.id)) {
                continue
            }
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
    }

    // TODO: Remove this function
    // Use this when adding product to cart in other places
    suspend fun addCartItem(productId: String, variantId: String, amount: Int): Result<Boolean> {
        val cartItem = CartItem(
            productId = productId,
            variantId = variantId,
            amount = amount
        )

        return cartItemRepository.addCartItemToCart(
            customerId = customerId,
            cartItem = cartItem
        )

    }

    fun updateCartItemAmountLocally(cartItemId: String, amountDelta: Int) {
        if (amountDelta == 0) {
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

    fun selectAllCartItems() {
        _selectedCartItemIds.value = _cartItems.value.map { it.id }.toSet()
    }

    fun unselectAllCartItems() {
        _selectedCartItemIds.value = emptySet()
    }

    fun removeSelectedCartItemLocally() {
        selectedCartItems.value.forEach { cartItem ->
            if (!_originalAmount.containsKey(cartItem.id)) {
                _originalAmount[cartItem.id] = cartItem.amount
            }

            _cartItems.value = _cartItems.value.minus(cartItem)
            _cartItems.value = _cartItems.value.plus(cartItem.copy(amount = 0))

            // _selectedCartItemIds.value = _selectedCartItemIds.value.minus(cartItem.id)
        }
    }

    fun onCartItemSelectionChanged(cartItemId: String, isSelected: Boolean) {
        _cartItems.value.find { it.id == cartItemId }?.let {
            if (isSelected) {
                _selectedCartItemIds.value = _selectedCartItemIds.value.plus(it.id)
            } else {
                _selectedCartItemIds.value = _selectedCartItemIds.value.minus(it.id)
            }
        }
    }

    suspend fun commitChangesToDatabase(): Result<Boolean> {
        val result = cartItemRepository.updateCartItemsAmount(
            customerId = customerId,
            adjustmentInfo = _cartItems.value
                .filter { _originalAmount[it.id] != null && it.amount != _originalAmount[it.id] }
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

    val checkingOutHandler: (
        onCheckoutConfirmation: (() -> Unit)?,
        onSuccessfulCheckout: (() -> Unit)?,
        onCheckoutFailure: ((reason: String) -> Unit)?,
    ) -> (() -> Unit) = { onCheckoutConfirmation,  onSuccessfulCheckout, onCheckoutFailure ->
        {
            viewModelScope.launch {
                onCheckingOut(onCheckoutConfirmation, onSuccessfulCheckout, onCheckoutFailure)
            }
        }
    }

    private var _cachedCheckoutHandler: Triple<
        (() -> Unit)?,
        (() -> Unit)?,
        ((reason: String) -> Unit)?
        > = Triple(null, null, null)
    val reconfirmOnInvalidItems: () -> Unit = {
        _isReconfirmedOnInvalidItems = true
        viewModelScope.launch {
            onCheckingOut(
                _cachedCheckoutHandler.first,
                _cachedCheckoutHandler.second,
                _cachedCheckoutHandler.third,
                )
        }
    }

    private var _isReconfirmedOnInvalidItems = false
    private suspend fun onCheckingOut(
        onCheckingOutConfirmation: (() -> Unit)? = null,
        onSuccessfulCheckingOut: (() -> Unit)? = null,
        onCheckingOutFailure: ((reason: String) -> Unit)? = null,
    ) {
        // The UI should have already disable checkout for invalid-only items
        if (validSelectedItems.value.isEmpty()) {
            onCheckingOutFailure?.invoke("No valid item(s) selected")
            return
        }

        if (
            selectedCartItems.value.size != validSelectedItems.value.size &&
            onCheckingOutConfirmation != null &&
            !_isReconfirmedOnInvalidItems
        ) {
            _cachedCheckoutHandler = Triple(
                onCheckingOutConfirmation,
                onSuccessfulCheckingOut,
                onCheckingOutFailure
            )
            onCheckingOutConfirmation()
            return
        } else {
            _selectedCartItemIds.value = validSelectedItems.value.map { it.id }.toSet()
        }
        _isReconfirmedOnInvalidItems = false

        val checkoutSavingResult = cartItemRepository.saveCheckoutInfo(
            customerId = customerId,
            checkoutInfo = checkoutDetail()
        )
        if (checkoutSavingResult.isFailure) {
            onCheckingOutFailure?.invoke(
                "Can't save checkout info: ${checkoutSavingResult.exceptionOrNull()?.message}"
            )
            return
        }

        var clearSavingResult = cartItemRepository.clearCheckingOutItems(customerId)
        if (clearSavingResult.isFailure) {
            onCheckingOutFailure?.invoke(
                "Can't clear checking out items: ${clearSavingResult.exceptionOrNull()?.message}"
            )
            return
        }

        val itemsSavingResult = cartItemRepository.saveCheckingOutItems(
            customerId = customerId,
            checkingOutItems = validSelectedItems.value.map {
                CheckingOutItem(
                    cartItem = it,
                    product = cartItemInformation.value[it.id]?.first!!,
                    variant = cartItemInformation.value[it.id]?.second!!
                )
            }
        )
        if (itemsSavingResult.isFailure) {
            onCheckingOutFailure?.invoke(
                "Can't save checking out items: ${itemsSavingResult.exceptionOrNull()?.message}"
            )
            return
        }

        viewModelScope.launch {
            commitChangesToDatabase()
        }

        onSuccessfulCheckingOut?.invoke()
    }

}

