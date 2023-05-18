package com.knu.quickthink.data

import com.knu.quickthink.model.GoogleUserModel
import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.network.UserApiService
import timber.log.Timber
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor (
    private val authApiService : UserApiService
) {
    suspend fun login(googleUserModel: GoogleUserModel): NetworkResult<String> {
        Timber.tag("network").d("AuthRemoteDataSource login fun 호출")
        return authApiService.login(googleUserModel)
    }
}