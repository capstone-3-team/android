package com.knu.quickthink.data

import com.knu.quickthink.model.user.GoogleUserModel
import com.knu.quickthink.model.user.IntroductionResponse
import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.user.TokenValidResponse
import com.knu.quickthink.model.user.UserListResponse
import com.knu.quickthink.network.apiService.UserApiService
import com.knu.quickthink.screens.search.UserInfo
import timber.log.Timber
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor (
    private val userApiService : UserApiService
) {
    suspend fun login(googleUserModel: GoogleUserModel): NetworkResult<String> {
        Timber.tag("network").d("AuthRemoteDataSource login fun 호출")
        return userApiService.login(googleUserModel)
    }
    suspend fun fetchIntroduction(googleId: String) :NetworkResult<IntroductionResponse>{
        return userApiService.fetchIntroduction(googleId)
    }
    suspend fun updateIntroduction(googleId : String, introduction : String) :NetworkResult<String> {
        return userApiService.updateIntroduction(googleId,text = introduction)
    }

    suspend fun tokenValidCheck(token: String,googleId: String) : NetworkResult<TokenValidResponse>{
        return userApiService.tokenValidCheck(token,googleId)
    }

    suspend fun updateToken(token: String, googleId: String): NetworkResult<String> {
        return userApiService.updateToken(token,googleId)
    }
    suspend fun searchUsers(searchName: String): NetworkResult<UserListResponse> {
        return userApiService.searchUsers(searchName)
    }

    suspend fun searchUser(googleId: String): NetworkResult<UserInfo> {
        return userApiService.searchUser(googleId)
    }
}