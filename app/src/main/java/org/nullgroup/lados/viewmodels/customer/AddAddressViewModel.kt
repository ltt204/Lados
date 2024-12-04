package org.nullgroup.lados.viewmodels.customer

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import org.nullgroup.lados.data.repositories.interfaces.IUserAddressRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import org.nullgroup.lados.data.repositories.interfaces.VietnamProvinceService
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    userRepository: UserRepository,
    private val provinceService: VietnamProvinceService,
    private val userAddressRepository: IUserAddressRepository
) : ViewModel() {
    var isInfoChanged = mutableStateOf(false)
        private set

    var provincesUiState: MenuItemsUIState by mutableStateOf(MenuItemsUIState.Loading)
        private set
    var districtsUiState: MenuItemsUIState by mutableStateOf(MenuItemsUIState.Default())
        private set
    var wardsUiState: MenuItemsUIState by mutableStateOf(MenuItemsUIState.Default())
        private set

    private var cacheDistricts = MutableStateFlow<List<District>>(emptyList())
    private var cacheProvinces = MutableStateFlow<List<Province>>(emptyList())
    private var cacheWards = MutableStateFlow<List<Ward>>(emptyList())

    var userAddress = MutableStateFlow(Address())
        private set

    var savingResult = mutableStateOf<SavingResult>(SavingResult.Loading)
        private set

    init {
        viewModelScope.launch {
            val currentUser = userRepository.getCurrentUser()
            userAddress = MutableStateFlow(
                Address(
                    userId = currentUser.id,
                    province = "",
                    district = "",
                    ward = ""
                )
            )
        }
        loadProvinces()
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
                    }
                    loadDistrict(provinceName)
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

    fun saveAddress() {
        viewModelScope.launch {
            try {
                Log.d("AddAddressViewModel", "saveAddress: ${userAddress.value}")
                userAddressRepository.saveAddress(userAddress.value)
                savingResult.value = SavingResult.Success
            } catch (e: Exception) {
                savingResult.value = SavingResult.Failed(e.message!!)
                Log.d("AddAddressViewModel", "Failed to save address")
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
        }
    }

    private fun loadDistrict(provinceName: String) {
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
        }
    }

    private fun loadWards(districtName: String) {
        viewModelScope.launch {
            delay(500)
            val districtId = cacheDistricts.value.first { it.full_name == districtName }.id
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

sealed class SavingResult {
    data object Success : SavingResult()
    data object Loading : SavingResult()
    data class Failed(val message: String) : SavingResult()
}