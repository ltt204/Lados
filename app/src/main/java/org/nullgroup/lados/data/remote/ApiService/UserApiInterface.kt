package org.nullgroup.lados.data.remote.ApiService

import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiInterface {

    @POST("enableUser")
    suspend fun enableUser(@Body uid: UserRequest): Response<Void>

    @POST("disableUser")
    suspend fun disableUser(@Body uid: UserRequest): Response<Void>
}

data class UserRequest(
    val uid: String,
)