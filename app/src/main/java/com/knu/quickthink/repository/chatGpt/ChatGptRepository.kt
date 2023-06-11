package com.knu.quickthink.repository.chatGpt

import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.chatGPT.ChatGptRequest
import com.knu.quickthink.model.chatGPT.ChatGptResponse

interface ChatGptRepository {

    suspend fun askToGpt(chatGptRequest: ChatGptRequest) : NetworkResult<ChatGptResponse>

}