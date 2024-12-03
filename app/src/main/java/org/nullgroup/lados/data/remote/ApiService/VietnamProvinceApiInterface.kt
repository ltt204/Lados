package org.nullgroup.lados.data.remote.ApiService

import org.nullgroup.lados.data.models.District
import org.nullgroup.lados.data.models.Province
import org.nullgroup.lados.data.models.Ward
import retrofit2.http.GET
import retrofit2.http.Path

interface VietnamProvinceApiInterface {
    @GET("1/0.htm")
    suspend fun getProvinces(): Response<Province>

    @GET("2/{provinceId}.htm")
    suspend fun getDistricts(@Path("provinceId") provinceId: String): Response<District>

    @GET("3/{districtId}.htm")
    suspend fun getWards(@Path("districtId") districtId: String): Response<Ward>
}

data class Response<T>(
    val error: String,
    val error_text: String,
    val data_name: String,
    val data: List<T>,
)