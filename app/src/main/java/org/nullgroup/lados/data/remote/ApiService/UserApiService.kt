package org.nullgroup.lados.data.remote.ApiService

import javax.inject.Inject

class UserApiService @Inject constructor(
    private val apiService: UserApiInterface,
) {
    suspend fun enableUser(uid: String) {
        apiService.enableUser(UserRequest(uid))
    }

    suspend fun disableUser(uid: String) {
        apiService.disableUser(UserRequest(uid))
    }
}