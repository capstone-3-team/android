package com.knu.quickthink.network.apiService

import com.knu.quickthink.BuildConfig.OPENAI_KEY
import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.chatGPT.ChatGptRequest
import com.knu.quickthink.model.chatGPT.ChatGptResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatGptApiService {
    @POST("v1/chat/completions")
    suspend fun askToGpt(
        @Body chatGptRequest: ChatGptRequest
    ): NetworkResult<ChatGptResponse>

}