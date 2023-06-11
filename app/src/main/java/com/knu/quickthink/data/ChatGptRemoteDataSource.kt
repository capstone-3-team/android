package com.knu.quickthink.data

import com.knu.quickthink.BuildConfig
import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.chatGPT.ChatGptRequest
import com.knu.quickthink.model.chatGPT.ChatGptResponse
import com.knu.quickthink.network.apiService.ChatGptApiService
import javax.inject.Inject

class ChatGptRemoteDataSource @Inject constructor(
    private val chatGptApiService: ChatGptApiService,
) {

    suspend fun askToGpt(chatGptRequest: ChatGptRequest) : NetworkResult<ChatGptResponse> {
        return chatGptApiService.askToGpt(chatGptRequest)
    }

}