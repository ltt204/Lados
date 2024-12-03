package org.nullgroup.lados.data.remote.ApiService

import android.util.Log
import org.nullgroup.lados.data.models.District
import org.nullgroup.lados.data.models.Province
import org.nullgroup.lados.data.models.Ward
import org.nullgroup.lados.data.repositories.interfaces.VietnamProvinceService

class NetworkVietnamProvinceService(private val apiService: VietnamProvinceApiInterface) : VietnamProvinceService {

    override suspend fun getProvinces(): List<Province> {
        val response = apiService.getProvinces()
        Log.d("NetworkVietnamProvinceRepository", "getProvinces: $response")
        return response.data
    }

    override suspend fun getDistricts(provinceId: String): List<District> {
        val response = apiService.getDistricts(provinceId )
        Log.d("NetworkVietnamProvinceRepository", "getProvinces: $response")
        return response.data
    }

    override suspend fun getWards(districtId: String): List<Ward> {
        val response = apiService.getWards(districtId)
        Log.d("NetworkVietnamProvinceRepository", "getProvinces: $response")
        return response.data
    }

    override suspend fun getProvinceByName(provinceName: String): Province {
        val response = apiService.getProvinces().data.first { it.name == provinceName }
        Log.d("NetworkVietnamProvinceRepository", "getProvinces: $response")
        return response
    }

    override suspend fun getDistrictByName(provinceId: String, districtName: String): District {
        val response = apiService.getDistricts(provinceId).data.first { it.name == districtName }
        Log.d("NetworkVietnamProvinceRepository", "getProvinces: $response")
        return response
    }

    override suspend fun getWardByName(districtId: String, wardName: String): Ward {
        val response = apiService.getWards(districtId).data.first { it.name == wardName }
        Log.d("NetworkVietnamProvinceRepository", "getProvinces: $response")
        return response
    }
}