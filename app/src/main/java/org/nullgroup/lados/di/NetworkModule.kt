package org.nullgroup.lados.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.nullgroup.lados.data.remote.ApiService.VietnamProvinceApiInterface
import org.nullgroup.lados.data.remote.ApiService.NetworkVietnamProvinceService
import org.nullgroup.lados.data.repositories.interfaces.common.VietnamProvinceService
import org.nullgroup.lados.utilities.VIETNAM_PROVINCE_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson() : Gson {
        return GsonBuilder().setLenient().create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(VIETNAM_PROVINCE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideProvinceApiService(retrofit: Retrofit): VietnamProvinceApiInterface {
        return retrofit.create(VietnamProvinceApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideProvinceRepository(apiService: VietnamProvinceApiInterface): VietnamProvinceService {
        return NetworkVietnamProvinceService(apiService)
    }
}