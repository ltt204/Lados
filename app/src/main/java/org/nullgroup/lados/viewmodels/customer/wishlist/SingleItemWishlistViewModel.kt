package org.nullgroup.lados.viewmodels.customer.wishlist

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.WishlistItem
import org.nullgroup.lados.data.repositories.interfaces.wishlist.WishlistItemRepository
import javax.inject.Inject

@HiltViewModel
class SingleItemWishlistViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val wishlistRepository: WishlistItemRepository,
    @ApplicationContext private val appContext: Context
): ViewModel() {
    private val customerId = firebaseAuth.currentUser?.uid

    private val _uiState = MutableStateFlow<SingleItemWishlistUiState>(SingleItemWishlistUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var _isInWishListWhenLoaded: Boolean? = null
    private var _productId: String? = null
    private var _lastUpdateTimestamp: Timestamp = Timestamp.now()

    fun checkIfInWishList(productId: String) {
        if (customerId == null) {
            _uiState.value = SingleItemWishlistUiState.Error("User not logged in")
            return
        }

        _uiState.value = SingleItemWishlistUiState.Loading
        _productId = productId
        _lastUpdateTimestamp = Timestamp.now()
        viewModelScope.launch {
            wishlistRepository.checkIfItemIsInWishlist(customerId, productId).collect {
                _uiState.value = SingleItemWishlistUiState.Success(it)
//                if (_isInWishListWhenLoaded == null) {
//                    _isInWishListWhenLoaded = it
//                }
                _isInWishListWhenLoaded = it
            }
        }
    }

    val toggleWishList: () -> Unit = fun() {
        if (customerId == null || _uiState.value !is SingleItemWishlistUiState.Success) {
            return
        }

        _uiState.update {
            SingleItemWishlistUiState.Success(
                !(_uiState.value as SingleItemWishlistUiState.Success).isInWishList)
        }
        _lastUpdateTimestamp = Timestamp.now()
    }

    override fun onCleared() {
        super.onCleared()

        if (_productId == null || customerId == null) {
            return
        }

        if (_uiState.value is SingleItemWishlistUiState.Success) {
            val isInWishList = (_uiState.value as SingleItemWishlistUiState.Success).isInWishList
            if (_isInWishListWhenLoaded == null || isInWishList == _isInWishListWhenLoaded) {
                return
            }

            val data = workDataOf(
                SingleItemWishlistUpdateWork.CUSTOMER_ID_KEY to customerId,
                SingleItemWishlistUpdateWork.PRODUCT_ID_KEY to _productId,
                SingleItemWishlistUpdateWork.IS_IN_WISHLIST_KEY to isInWishList,
                SingleItemWishlistUpdateWork.TIMESTAMP_KEY to _lastUpdateTimestamp.seconds
            )
            val updateWorkRequest = OneTimeWorkRequestBuilder<SingleItemWishlistUpdateWork>()
                .setInputData(data)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(appContext).enqueue(updateWorkRequest)
        }
    }
}

sealed class SingleItemWishlistUiState {
    object Loading : SingleItemWishlistUiState()
    data class Success(val isInWishList: Boolean) : SingleItemWishlistUiState()
    data class Error(val message: String) : SingleItemWishlistUiState()
}

@HiltWorker
class SingleItemWishlistUpdateWork @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val wishlistRepository: WishlistItemRepository,
): CoroutineWorker(context, workerParams) {
    companion object {
        const val CUSTOMER_ID_KEY = "customerId"
        const val PRODUCT_ID_KEY = "productId"
        const val IS_IN_WISHLIST_KEY = "isInWishList"
        const val TIMESTAMP_KEY = "timestamp"
    }

    override suspend fun doWork() = coroutineScope {
        val customerId = inputData.getString(CUSTOMER_ID_KEY) ?: return@coroutineScope Result.failure()
        val productId = inputData.getString(PRODUCT_ID_KEY) ?: return@coroutineScope Result.failure()
        val isInWishList = inputData.getBoolean(IS_IN_WISHLIST_KEY, false)
        val timestamp = Timestamp(inputData.getLong(TIMESTAMP_KEY, 0), 0)

        if (isInWishList) {
            wishlistRepository.addItemsToWishlist(customerId, listOf(WishlistItem(productId = productId, addedAt = timestamp)))
        } else {
            wishlistRepository.removeProductFromWishlist(customerId, productId)
        }

        Log.d("SingleItemWishlistUpdateWork", "Updated wishlist for $customerId for product $productId")

        Result.success()
    }
}

