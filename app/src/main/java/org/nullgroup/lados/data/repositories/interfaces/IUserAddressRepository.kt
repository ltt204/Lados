package org.nullgroup.lados.data.repositories.interfaces

import kotlinx.coroutines.flow.Flow
import org.nullgroup.lados.data.models.Address

interface IUserAddressRepository {
    fun getAddressesFlow(): Flow<List<Address>>
    fun getSingleAddressFlow(addressId: String): Flow<Address>
    suspend fun getSingleAddress(address: Address): Address
    suspend fun saveAddress(address: Address)
    suspend fun addAddress(address: Address)
    suspend fun deleteAddress(addressId: String)
}