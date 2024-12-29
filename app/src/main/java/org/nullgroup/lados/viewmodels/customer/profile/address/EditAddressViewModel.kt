package org.nullgroup.lados.viewmodels.customer.profile.address

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Address
import org.nullgroup.lados.data.models.District
import org.nullgroup.lados.data.models.Province
import org.nullgroup.lados.data.models.Ward
import org.nullgroup.lados.data.repositories.interfaces.user.IUserAddressRepository
import org.nullgroup.lados.data.repositories.interfaces.common.VietnamProvinceService
import org.nullgroup.lados.screens.Screen
import javax.inject.Inject

@HiltViewModel
class EditAddressViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userAddressRepository: IUserAddressRepository,
    private val provinceService: VietnamProvinceService
) : ViewModel() {
    private val addressId: String =
        checkNotNull(savedStateHandle.get<String>(Screen.Customer.Address.EditAddress.ID_ARG))
    private var cacheProvinces = MutableStateFlow<List<Province>>(emptyList())
    private var cacheDistricts = MutableStateFlow<List<District>>(emptyList())
    private var cacheWards = MutableStateFlow<List<Ward>>(emptyList())

    var isInfoChanged = mutableStateOf(false)
        private set

    var provincesUiState: MenuItemsUIState by mutableStateOf(MenuItemsUIState.Loading)
        private set
    var districtsUiState: MenuItemsUIState by mutableStateOf(MenuItemsUIState.Default())
        private set
    var wardsUiState: MenuItemsUIState by mutableStateOf(MenuItemsUIState.Default())
        private set
    val userAddress = MutableStateFlow(Address())

    var savingResult = mutableStateOf<SavingResult>(SavingResult.Loading)
        private set

    init {
        loadProvinces()
        viewModelScope.launch {
            userAddressRepository.getSingleAddressFlow(addressId).collect {
                userAddress.emit(it)
                Log.d("EditAddressViewModel", "init: $userAddress")
            }
        }
    }

    fun onProvinceSelected(index: Int) {
        provincesUiState.let {
            when (it) {
                is MenuItemsUIState.Success -> {
                    val provinceName = it.data[index]
                    districtsUiState = MenuItemsUIState.Loading
                    wardsUiState = MenuItemsUIState.Default()

                    viewModelScope.launch {
                        userAddress.emit(
                            userAddress.value.copy(
                                province = provinceName,
                                district = "",
                                ward = ""
                            )
                        )
                        isInfoChanged.value = true
                    }
                    loadDistricts(provinceName)
                }

                else -> return
            }
        }
    }

    fun onDistrictSelected(index: Int) {
        districtsUiState.let {
            when (it) {
                is MenuItemsUIState.Success -> {
                    val districtName = it.data[index]
                    wardsUiState = MenuItemsUIState.Loading
                    viewModelScope.launch {
                        userAddress.emit(
                            userAddress.value.copy(
                                district = districtName,
                                ward = ""
                            )
                        )
                        isInfoChanged.value = true
                    }
                    loadWards(districtName)
                }

                else -> return
            }
        }
    }

    fun onWardSelected(index: Int) {
        wardsUiState.let {
            when (it) {
                is MenuItemsUIState.Success -> {
                    val ward = it.data[index]
                    viewModelScope.launch {
                        userAddress.emit(
                            userAddress.value.copy(
                                ward = ward
                            )
                        )
                        isInfoChanged.value = true
                    }
                }

                else -> return
            }
        }
    }

    fun onStreetDetailChanged(street: String) {
        viewModelScope.launch {
            delay(500)
            userAddress.value.detail = street
            isInfoChanged.value = true
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            userAddressRepository.deleteAddress(addressId)
        }
    }

    fun saveAddress() {
        viewModelScope.launch {
            Log.d("EditAddressViewModel", "saveAddress: ${userAddress.value}")
            try {
                userAddressRepository.saveAddress(userAddress.value)
                savingResult.value = SavingResult.Success
            } catch (e: Exception) {
                savingResult.value = SavingResult.Failed(e.message!!)
            }
        }
    }

    private fun loadProvinces() {
        viewModelScope.launch {
            delay(500)
            if (cacheProvinces.value.isNotEmpty()) {
                provincesUiState =
                    MenuItemsUIState.Success(cacheProvinces.value.map { it.full_name })
                return@launch
            }
            try {
                provincesUiState = MenuItemsUIState.Loading
                provinceService.getProvinces().let {
                    provincesUiState = MenuItemsUIState.Success(it.map { it.full_name })
                    cacheProvinces.emit(it)
                }
            } catch (e: Exception) {
                provincesUiState = MenuItemsUIState.Failed(e.message!!)
            }

            if (userAddress.value.district.isNotEmpty()) {
                loadDistricts(userAddress.value.province)
            } else {
                // Do nothing
            }
        }
    }

    private fun loadDistricts(provinceName: String) {
        viewModelScope.launch {
            delay(500)

            val provinceId = cacheProvinces.value.first { it.full_name == provinceName }.id
            try {
                provinceService.getDistricts(provinceId).let {
                    districtsUiState = MenuItemsUIState.Success(it.map { it.full_name })
                    cacheDistricts.emit(it)
                }
            } catch (e: Exception) {
                districtsUiState = MenuItemsUIState.Failed(e.message!!)
            }

            if (userAddress.value.ward.isNotEmpty()) {
                loadWards(userAddress.value.district)
            } else {
                // Do nothing
            }
        }
    }

    private fun loadWards(districtName: String) {
        viewModelScope.launch {
            delay(500)

            Log.d("EditAddressViewModel", "loadWards: $districtName")
            val districtId = cacheDistricts.value.first { it.full_name == districtName }.id
            Log.d("EditAddressViewModel", "loadWards: $districtId")
            try {
                provinceService.getWards(districtId).let {
                    wardsUiState = MenuItemsUIState.Success(it.map { it.full_name })
                    cacheWards.emit(it)
                }
            } catch (e: Exception) {
                wardsUiState = MenuItemsUIState.Failed(e.message!!)
            }
        }
    }
}

sealed interface MenuItemsUIState {
    data class Default(var data: List<String> = emptyList()) : MenuItemsUIState
    data class Success(var data: List<String>) : MenuItemsUIState
    data object Loading : MenuItemsUIState
    data class Failed(var message: String) : MenuItemsUIState
}
