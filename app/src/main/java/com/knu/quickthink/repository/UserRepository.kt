package com.knu.quickthink.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.knu.quickthink.model.GoogleUserModel
import com.knu.quickthink.model.IntroductionResponse
import com.knu.quickthink.model.NetworkResult

interface UserRepository {
    suspend fun login(gsa :GoogleSignInAccount): NetworkResult<String>

    suspend fun fetchIntroduction() : NetworkResult<IntroductionResponse>
    suspend fun updateIntroduction(introduction : String) :NetworkResult<String>
}