package org.nullgroup.lados.viewmodels.customer.wishlist

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Product
import org.nullgroup.lados.data.models.WishlistItem
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.data.repositories.interfaces.wishlist.WishlistItemRepository
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val wishlistRepository: WishlistItemRepository,
    private val productRepository: ProductRepository,
    @ApplicationContext private val appContext: Context, // Not really used
) : ViewModel() {
    private val _wishlistUiState = MutableStateFlow<WishlistUiState>(WishlistUiState.Loading)
    val wishlistUiState: StateFlow<WishlistUiState> = _wishlistUiState.asStateFlow()

    private val _loadedWishlistItems = MutableStateFlow<MutableList<WishlistItem>>(mutableListOf())
    private val _addingWishlistItems: MutableList<WishlistItem> = mutableListOf()
    private val _removingWishlistItems: MutableList<WishlistItem> = mutableListOf()

    private val _productInformation = MutableStateFlow<Map<String, Product>?>(null)
    val productInformation = _productInformation.asStateFlow()

    private fun updateCurrentItemsFlow () {
        _wishlistUiState.value = WishlistUiState.Success(
            _loadedWishlistItems.value.plus(_addingWishlistItems).minus(_removingWishlistItems)
        )
    }

    private val customerId = firebaseAuth.currentUser?.uid
    init {
        fetchWishList()
    }

    private fun fetchWishList() {
        if (customerId == null) {
            _wishlistUiState.value = WishlistUiState.Error("User not logged in")
            return
        }
        viewModelScope.launch {
            wishlistRepository.getWishlistItems(customerId)
                .flowOn(Dispatchers.IO)
                .catch {
                    _wishlistUiState.value = WishlistUiState.Error(it.message ?: "An error occurred")
                }
                .collect { items ->
                    _loadedWishlistItems.value = items.toMutableList()
                    _addingWishlistItems.removeIf { item -> items.any { it.productId == item.productId } }
                    _removingWishlistItems.removeIf { item -> items.none { it.productId == item.productId } }
                    _wishlistUiState.value = WishlistUiState.Success(
                        items.plus(_addingWishlistItems).minus(_removingWishlistItems)
                    )
                }
        }
    }

    fun fetchProductInfo() {
        viewModelScope.launch {
            _loadedWishlistItems.collect { items ->
                val newItemIds = items
                    .filter { _productInformation.value?.containsKey(it.productId) != true }
                    .map { it.productId }
                if (newItemIds.isEmpty()) {
                    return@collect
                }

                newItemIds.forEach { productId ->
                    viewModelScope.launch(Dispatchers.IO) {
                        productRepository.getProductByIdFlow(productId)
                            .firstOrNull()
                            ?.let { product ->
                                val newInfo = mapOf(productId to product)
                                _productInformation.update { it?.plus(newInfo) ?: newInfo }
                            }
                    }
                }

            }

        }
    }

    private val isInWishList: (String) -> Boolean = { productId ->
        (_wishlistUiState.value
            .let { (it as? WishlistUiState.Success)?.items }
            ?.any { it.productId == productId }) == true
    }

    val switchWishListState: (String) -> Unit = { productId ->
        if (isInWishList(productId)) {
            removeWishlistItem(productId)
        } else {
            addWishlistItem(productId)
        }
    }

    fun addWishlistItem(productId: String) {
//        // Should be checked by "switchWishListState" before calling this function
//        // If the item is already in the adding list, do nothing
//        val addingItemRef = _addingWishlistItems.find { it.productId == productId }
//        if (addingItemRef != null) {
//            return
//        }
//

//        // If the item is already in the wishlist (loaded list), do nothing
//        if (_loadedWishlistItems.any { it.productId == productId }) {
//            return
//        }

        // If the item is in the removing list, remove the item in that list instead
        val removingItemRef = _removingWishlistItems.find { it.productId == productId }
        if (removingItemRef != null) {
            _removingWishlistItems.remove(removingItemRef)

        } else {
            // If the item is already in the adding list, do nothing
            if (_loadedWishlistItems.value.any { it.productId == productId }) {
                return
            }

            _addingWishlistItems.add(WishlistItem(productId = productId))
        }

        updateCurrentItemsFlow()
    }



    fun removeWishlistItem(productId: String) {
        // If the item is already in the adding list, remove the item in that list instead
        val addingItemRef = _addingWishlistItems.find { it.productId == productId }
        if (addingItemRef != null) {
            _addingWishlistItems.remove(addingItemRef)
        } else {
            val removingItem = _loadedWishlistItems.value.find { it.productId == productId }
                ?: return // If the item is not in the wishlist (loaded list), do nothing

            // There exists an item in the wishlist (loaded list) with the same productId
            _removingWishlistItems.add(removingItem)
        }

//        val removingItem = _wishlistUiState.value
//            .let { (it as? WishlistUiState.Success)?.items }
//            ?.find { it.productId == productId }
//            ?: return
//        _removingWishlistItems.add(removingItem)

        updateCurrentItemsFlow()
    }

    fun commitChangesToDatabase() {
        if (customerId == null) {
            Log.e("WishlistViewModel", "User not logged in")
            return
        }

        if (_removingWishlistItems.isNotEmpty()) {
            val removingItemIds = _removingWishlistItems.map { it.id }
            viewModelScope.launch(Dispatchers.IO) {
                val result = wishlistRepository.removeItemsFromWishlist(customerId, removingItemIds)
                if (result.isSuccess) {
                    _removingWishlistItems.clear()
                }
            }
        }

        if (_addingWishlistItems.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = wishlistRepository.addItemsToWishlist(customerId, _addingWishlistItems)
                if (result.isSuccess) {
                    _addingWishlistItems.clear()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (_addingWishlistItems.isEmpty() && _removingWishlistItems.isEmpty()) {
            return
        }

        val updateData = Data.Builder()
            .putString(WishlistUpdateWork.CUSTOMER_ID_KEY, customerId)
            .putStringArray(WishlistUpdateWork.ADDING_ITEMS_KEY, _addingWishlistItems.map { it.toJsonString() }.toTypedArray())
            .putStringArray(WishlistUpdateWork.REMOVING_ITEM_IDS_KEY, _removingWishlistItems.map { it.id }.toTypedArray())
            .build()
        val updateWorkRequest = OneTimeWorkRequestBuilder<WishlistUpdateWork>()
            .setInputData(updateData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        WorkManager.getInstance(appContext).enqueue(updateWorkRequest)
    }
}

sealed class WishlistUiState {
    data object Loading : WishlistUiState()
    data class Success(val items: List<WishlistItem>) : WishlistUiState()
    data class Error(val message: String) : WishlistUiState()
}

// See binding guide in https://developer.android.com/training/dependency-injection/hilt-jetpack?source=post_page-----b60046ff7f02--------------------------------#workmanager
@HiltWorker
class WishlistUpdateWork @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val wishlistRepository: WishlistItemRepository,
): CoroutineWorker(context, workerParams) {
    companion object {
        const val CUSTOMER_ID_KEY = "customerId"
        const val ADDING_ITEMS_KEY = "addingItems"
        const val REMOVING_ITEM_IDS_KEY = "removingItemIds"
    }

    override suspend fun doWork() = coroutineScope {
        val customerId = inputData.getString(CUSTOMER_ID_KEY)
            ?: return@coroutineScope Result.failure().also { Log.e("WishlistUpdateWork", "No customer ID") }
        val addingItems = inputData.getStringArray(ADDING_ITEMS_KEY)
            ?.mapNotNull { WishlistItem.fromJsonString(it) }
            ?: emptyList()
        val removingItemIds = inputData.getStringArray(REMOVING_ITEM_IDS_KEY)
            ?: emptyArray()

        val addingDeferred = async {
            wishlistRepository.addItemsToWishlist(customerId, addingItems)
        }
        val removingDeferred = async {
            wishlistRepository.removeItemsFromWishlist(customerId, removingItemIds.toList())
        }
        val result = awaitAll(addingDeferred, removingDeferred)

        if (result[0].isFailure) {
            Log.e("WishlistUpdateWork", "Error adding to wishlist: ${result[0].exceptionOrNull()}")
        }
        if (result[1].isFailure) {
            Log.e("WishlistUpdateWork", "Error removing from wishlist: ${result[1].exceptionOrNull()}")
        }
        if (result.all { it.isSuccess }) {
            Result.success()
        } else {
            Result.failure()
        }
    }
}