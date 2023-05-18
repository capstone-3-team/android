package com.knu.quickthink.repository

import com.knu.quickthink.data.UserRemoteDataSource
import com.knu.quickthink.model.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {
    override suspend fun login(googleUserModel: GoogleUserModel): NetworkResult<String>
        = remoteDataSource.login(googleUserModel)
}