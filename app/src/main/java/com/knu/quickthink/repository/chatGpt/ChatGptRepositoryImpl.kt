package com.knu.quickthink.repository.chatGpt

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.knu.quickthink.data.ChatGptRemoteDataSource
import com.knu.quickthink.data.UserRemoteDataSource
import com.knu.quickthink.data.UserTokenDataStore
import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.chatGPT.ChatGptRequest
import com.knu.quickthink.model.chatGPT.ChatGptResponse
import com.knu.quickthink.network.UserManager
import com.knu.quickthink.repository.user.UserRepository
import javax.inject.Inject

class ChatGptRepositoryImpl @Inject constructor(
    private val remoteDataSource: ChatGptRemoteDataSource,
) : ChatGptRepository {

    override suspend fun askToGpt(chatGptRequest: ChatGptRequest): NetworkResult<ChatGptResponse> {
        return remoteDataSource.askToGpt(chatGptRequest)
    }

}