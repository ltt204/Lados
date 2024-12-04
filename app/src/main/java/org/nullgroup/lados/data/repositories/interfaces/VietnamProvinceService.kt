package org.nullgroup.lados.data.repositories.interfaces

import org.nullgroup.lados.data.models.District
import org.nullgroup.lados.data.models.Province
import org.nullgroup.lados.data.models.Ward

interface VietnamProvinceService {
    suspend fun getProvinces(): List<Province>
    suspend fun getDistricts(provinceId: String): List<District>
    suspend fun getWards(districtId: String): List<Ward>

    suspend fun getProvinceByName(provinceName: String): Province
    suspend fun getDistrictByName(provinceId: String, districtName: String): District
    suspend fun getWardByName(districtId: String, wardName: String): Ward
}