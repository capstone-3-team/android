package com.knu.quickthink.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.knu.quickthink.UserToken
import com.knu.quickthink.model.GoogleUserModel
import com.knu.quickthink.model.IntroductionResponse
import com.knu.quickthink.model.NetworkResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(gsa :GoogleSignInAccount): NetworkResult<String>
    suspend fun autoLogin() : Boolean

    suspend fun logOut() : Boolean

    suspend fun fetchIntroduction() : NetworkResult<IntroductionResponse>
    suspend fun updateIntroduction(introduction : String) :NetworkResult<String>
}