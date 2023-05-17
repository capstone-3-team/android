package com.knu.quickthink.repository

import com.knu.quickthink.data.AuthRemoteDataSource
import com.knu.quickthink.model.*
import com.knu.quickthink.network.RetrofitFailureStateException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun login(googleUserModel: GoogleUserModel): NetworkResult<String>
        = remoteDataSource.login(googleUserModel)
}