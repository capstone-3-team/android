package com.knu.quickthink.network

import com.knu.quickthink.model.GoogleUserModel
import com.knu.quickthink.model.NetworkResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("/api/auth")
    suspend fun login(
        @Body googleUserModel: GoogleUserModel
    ) : NetworkResult<String>


}