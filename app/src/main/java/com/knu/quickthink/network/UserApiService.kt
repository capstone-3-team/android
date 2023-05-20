package com.knu.quickthink.network

import com.knu.quickthink.model.GoogleUserModel
import com.knu.quickthink.model.IntroductionResponse
import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.TokenValidResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApiService {

    @POST("/api/auth")
    suspend fun login(
        @Body googleUserModel: GoogleUserModel
    ) : NetworkResult<String>

    @GET("/api/token")
    suspend fun tokenValidCheck(
        @Query("accessToken") accessToken: String,
        @Query("googleId") googleId: String
    ) :NetworkResult<TokenValidResponse>

    // 현재 구현 안되어 있음
    @GET("/api/updateToken")
    suspend fun updateToken(
        @Query("accessToken") accessToken: String,
        @Query("googleId") googleId: String
    ) :NetworkResult<String>

    @GET("/api/profile")
    suspend fun fetchIntroduction(
        @Header("accessToken") accessToken: String,
        @Query("googleId") googleId: String
    ) :NetworkResult<IntroductionResponse>

    @POST("/api/profile")
    suspend fun updateIntroduction(
        @Header("accessToken") accessToken: String,
        @Query("googleId") googleId : String,
        @Body text: String
    ) : NetworkResult<String>



}