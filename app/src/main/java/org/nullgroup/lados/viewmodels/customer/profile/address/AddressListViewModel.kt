package org.nullgroup.lados.viewmodels.customer.profile.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.repositories.interfaces.IUserAddressRepository
import javax.inject.Inject

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val userAddressRepository: IUserAddressRepository
) : ViewModel() {
    val userAddresses = userAddressRepository.getAddressesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            userAddressRepository.deleteAddress(addressId)
        }
    }
}