package org.nullgroup.lados.viewmodels.customer

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.models.Address
import org.nullgroup.lados.data.models.District
import org.nullgroup.lados.data.models.Province
import org.nullgroup.lados.data.models.Ward
import org.nullgroup.lados.data.repositories.interfaces.IUserAddressRepository
import org.nullgroup.lados.data.repositories.interfaces.VietnamProvinceService
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val provinceService: VietnamProvinceService,
    private val userAddressRepository: IUserAddressRepository
) : ViewModel() {
    private val currentUser = firebaseAuth.currentUser!!.email?.let {
        firestore.collection("users").document(
            it
        )
    }

    var isInfoChanged = mutableStateOf(false)
        private set

    var provincesUiState: MenuItemsUIState by mutableStateOf(MenuItemsUIState.Loading)
        private set
    var districtsUiState: MenuItemsUIState by mutableStateOf(MenuItemsUIState.Loading)
        private set
    var wardsUiState: MenuItemsUIState by mutableStateOf(MenuItemsUIState.Loading)
        private set

    private var cacheDistricts = MutableStateFlow<List<District>>(emptyList())
    private var cacheProvinces = MutableStateFlow<List<Province>>(emptyList())
    private var cacheWards = MutableStateFlow<List<Ward>>(emptyList())

    var userAddress = MutableStateFlow(Address(userId = currentUser!!.id))
        private set

    init {
        loadProvinces()
    }

    fun onProvinceSelected(index: Int) {
        provincesUiState.let {
            when (it) {
                is MenuItemsUIState.Success -> {
                    val provinceName = it.data[index]
                    districtsUiState = MenuItemsUIState.Loading
                    wardsUiState = MenuItemsUIState.Loading

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

    fun saveAddress() {
        viewModelScope.launch {
            try {
                Log.d("AddAddressViewModel", "saveAddress: ${userAddress.value}")
                userAddressRepository.saveAddress(userAddress.value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadProvinces() {
        viewModelScope.launch {
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
                provincesUiState = MenuItemsUIState.Failed
            }
        }
    }

    private fun loadDistrict(provinceName: String) {
        viewModelScope.launch {
            if (cacheDistricts.value.isNotEmpty()) {
                districtsUiState =
                    MenuItemsUIState.Success(cacheDistricts.value.map { it.full_name })
                return@launch
            }
            val provinceId = cacheProvinces.value.first { it.full_name == provinceName }.id
            try {
                provinceService.getDistricts(provinceId).let {
                    districtsUiState = MenuItemsUIState.Success(it.map { it.full_name })
                    cacheDistricts.emit(it)
                }
            } catch (e: Exception) {
                districtsUiState = MenuItemsUIState.Failed
            }
        }
    }

    private fun loadWards(districtName: String) {
        viewModelScope.launch {
            if (cacheWards.value.isNotEmpty()) {
                wardsUiState = MenuItemsUIState.Success(cacheWards.value.map { it.full_name })
                return@launch
            }
            val districtId = cacheDistricts.value.first { it.full_name == districtName }.id
            try {
                provinceService.getWards(districtId).let {
                    wardsUiState = MenuItemsUIState.Success(it.map { it.full_name })
                    cacheWards.emit(it)
                }
            } catch (e: Exception) {
                wardsUiState = MenuItemsUIState.Failed
            }
        }
    }
}