package com.knu.quickthink.repository

import com.knu.quickthink.model.GoogleUserModel
import com.knu.quickthink.model.NetworkResult

interface UserRepository {
    suspend fun login(googleUserModel: GoogleUserModel): NetworkResult<String>

}