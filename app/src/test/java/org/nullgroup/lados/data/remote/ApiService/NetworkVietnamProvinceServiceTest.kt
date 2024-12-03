package org.nullgroup.lados.data.remote.ApiService

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class NetworkVietnamProvinceServiceTest {

    @Mock
    private lateinit var apiService: VietnamProvinceApiInterface

    private lateinit var networkVietnamProvinceService: NetworkVietnamProvinceService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        networkVietnamProvinceService = NetworkVietnamProvinceService(apiService)
    }

    @Test
    fun getProvinces() {
    }

    @Test
    fun getDistricts() {
    }

    @Test
    fun getWards() {
    }
}