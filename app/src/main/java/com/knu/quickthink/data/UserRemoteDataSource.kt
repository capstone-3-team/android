package com.knu.quickthink.data

import com.knu.quickthink.model.user.GoogleUserModel
import com.knu.quickthink.model.user.IntroductionResponse
import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.user.TokenValidResponse
import com.knu.quickthink.network.apiService.UserApiService
import timber.log.Timber
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor (
    private val userApiService : UserApiService
) {
    suspend fun login(googleUserModel: GoogleUserModel): NetworkResult<String> {
        Timber.tag("network").d("AuthRemoteDataSource login fun 호출")
        return userApiService.login(googleUserModel)
    }
    suspend fun fetchIntroduction(accessToken :String, googleId: String) :NetworkResult<IntroductionResponse>{
        return userApiService.fetchIntroduction(accessToken, googleId)
    }
    suspend fun updateIntroduction(accessToken :String, googleId : String, introduction : String) :NetworkResult<String> {
        return userApiService.updateIntroduction(accessToken,googleId,text = introduction)
    }

    suspend fun tokenValidCheck(token: String,googleId: String) : NetworkResult<TokenValidResponse>{
        return userApiService.tokenValidCheck(token,googleId)
    }

    suspend fun updateToken(token: String, googleId: String): NetworkResult<String> {
        return userApiService.updateToken(token,googleId)
    }
}