package com.knu.quickthink.repository.user

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.knu.quickthink.model.user.IntroductionResponse
import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.user.UserListResponse
import com.knu.quickthink.screens.search.UserInfo

interface UserRepository {
    suspend fun login(gsa :GoogleSignInAccount): Result<String>
    suspend fun autoLogin() : Boolean

    suspend fun logOut() : Boolean

    suspend fun fetchIntroduction() : NetworkResult<IntroductionResponse>
    suspend fun updateIntroduction(introduction : String) :NetworkResult<String>

    suspend fun searchUsers(searchName : String) : NetworkResult<UserListResponse>
    suspend fun fetchUserInfo(googleId : String?) : NetworkResult<UserInfo>

}