package org.nullgroup.lados.viewmodels.customer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.nullgroup.lados.data.models.CartItem
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.ProductVariant
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    // private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
): ViewModel() {
    // Hardcode
    private val userEmail = "customer@test.com"

    private val _orderingItems = MutableStateFlow<List<CartItem>>(mutableListOf())
    val orderingItems = _orderingItems.asStateFlow()

    private val _orderingItemInformation =
        MutableStateFlow<Map<String, Pair<Product?, ProductVariant?>>>(mutableMapOf())
    val orderingItemInformation = _orderingItemInformation.asStateFlow()
}