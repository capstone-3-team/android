package com.knu.quickthink.repository.user

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.knu.quickthink.model.user.IntroductionResponse
import com.knu.quickthink.model.NetworkResult

interface UserRepository {
    suspend fun login(gsa :GoogleSignInAccount): NetworkResult<String>
    suspend fun autoLogin() : Boolean

    suspend fun logOut() : Boolean

    suspend fun fetchIntroduction() : NetworkResult<IntroductionResponse>
    suspend fun updateIntroduction(introduction : String) :NetworkResult<String>
}