package org.nullgroup.lados.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cartItemRepository: CartItemRepository
): ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(mutableListOf())
    var cartItems = _cartItems.asStateFlow()

    private val _cartItemInformation =
        MutableStateFlow<Map<String, Pair<Product?, ProductVariant?>>>(mutableMapOf())
    var cartItemInformation = _cartItemInformation.asStateFlow()

    // Hardcode
    private val userEmail = "customer@test.com"

    fun getCartItems(cartId: String) {
        if (userEmail.isEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                val result = cartItemRepository.getCartItems(cartId)
                if (result.isSuccess) {
                    _cartItems.value = result.getOrElse { emptyList() }
                }
            } catch (e: Exception) {
                TODO("Solve it later")
            }
        }
    }

    fun getItemsInformation() {
        viewModelScope.launch {
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
                    }
                } catch (_: Exception) {
                    _cartItemInformation.value = _cartItemInformation.value.plus(
                        cartItem.id to Pair(null, null)
                    )
                }
            }
        }
    }
}