package org.nullgroup.lados.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nullgroup.lados.data.models.Cart
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    // private val userRepository: UserRepository,
    private val cartItemRepository: CartItemRepository
): ViewModel() {

    private var _cart: Cart? = null
    val cart: Cart? = _cart

    private val _cartItems = MutableStateFlow<List<CartItem>>(mutableListOf())
    var cartItems = _cartItems.asStateFlow()

    private val _cartItemInformation =
        MutableStateFlow<Map<String, Pair<Product?, ProductVariant?>>>(mutableMapOf())
    var cartItemInformation = _cartItemInformation.asStateFlow()

    private val _cartItemSelection = MutableStateFlow<Map<String, Boolean>>(mutableMapOf())
    var cartItemSelection = _cartItemSelection.asStateFlow()

    // TODO: Hardcode
    private val userEmail = "customer@test.com"

    init {
        refreshCartInformation()
    }

    // TODO: Handle database change first

    fun refreshCartInformation() {
        viewModelScope.launch {
            val initResult = cartItemRepository.getOrInitializeCart(userEmail)

            if (initResult.isFailure || initResult.getOrNull() == null) {
                // Handle error
                withContext(Dispatchers.Main) {
                    // Show error message
                    Log.e("CartViewModel", "Error initializing cart: ", initResult.exceptionOrNull())
                    return@withContext
                }
            }

            _cart = initResult.getOrNull()

            // TODO: to be removed
            addCartItem("prod-12345", "var-001", 3)
            addCartItem("prod-12345", "var-002", 2)

            getCartItems()
            getItemsInformation()
        }
    }

    suspend fun getCartItems() {
        if (userEmail.isEmpty()) {
            return
        }

        if (_cart == null) {
            return
        }


        val result = cartItemRepository.getCartItems(_cart!!.customerId)
        _cartItems.value = result.fold(
            onSuccess = { it },
            onFailure = { emptyList() }
        )
    }

    suspend fun getItemsInformation() {
        for (cartItem in _cartItems.value) {
            try {
                val result = cartItemRepository.getCartItemInformation(
                    cartItem.productId,
                    cartItem.variantId
                )
                if (result.isSuccess) {
                    val (product, productVariant) = result.getOrNull()!!
                    _cartItemInformation.value = _cartItemInformation.value.plus(
                        cartItem.id to Pair(product, productVariant)
                    )
                } else {
                    _cartItemInformation.value = _cartItemInformation.value.plus(
                        cartItem.id to Pair(null, null)
                    )
                }
            } catch (_: Exception) {
                _cartItemInformation.value = _cartItemInformation.value.plus(
                    cartItem.id to Pair(null, null)
                )
            }
        }
    }

    fun addCartItem(productId: String, variantId: String, amount: Int) {
        if (_cart == null) {
            return
        }

        val cartItem = CartItem(
            productId = productId,
            variantId = variantId,
            amount = amount
        )
        viewModelScope.launch {
            var addResult = cartItemRepository.addCartItemToCart(
                cartId = _cart!!.customerId,
                cartItem = cartItem
            )

            // TODO: temporary solution
            if (addResult.isSuccess) {
                getCartItems()
                getItemsInformation()
            }
        }
    }

    fun updateCartItemAmount(cartItemId: String, amountDelta: Int) {
        if (_cart == null) {
            return
        }

        viewModelScope.launch {
            val updateResult = cartItemRepository.updateCartItemAmount(
                cartId = _cart!!.customerId,
                cartItemId = cartItemId,
                amountDelta = amountDelta,
            )

            // TODO: temporary solution
            if (updateResult.isSuccess) {
                // TODO: handle selection on update with deletion
                getCartItems()
            }
        }
    }

    fun removeCartItem(cartItemId: String) {
        if (_cart == null) {
            return
        }

        viewModelScope.launch {
            val removeResult = cartItemRepository.removeCartItemFromCart(
                cartId = _cart!!.customerId,
                cartItemId = cartItemId
            )

            // TODO: temporary solution
            if (removeResult.isSuccess) {
                _cartItemSelection.value = _cartItemSelection.value.minus(cartItemId)
                getCartItems()
            }
        }
    }

    fun onCartItemSelectionChanged(cartItemId: String, isSelected: Boolean) {
        _cartItemSelection.value = _cartItemSelection.value.plus(cartItemId to isSelected)
    }
}