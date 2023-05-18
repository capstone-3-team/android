package com.knu.quickthink.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.knu.quickthink.data.UserRemoteDataSource
import com.knu.quickthink.model.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val googleSignInClient: GoogleSignInClient
) : UserRepository {

    private lateinit var googleAccount :GoogleSignInAccount

    override suspend fun login(gsa :GoogleSignInAccount): NetworkResult<String>{
        googleAccount = gsa
        val googleUserModel = GoogleUserModel(
            token = googleAccount.serverAuthCode!!,
            googleId = googleAccount.id!!,
            googleName = googleAccount.displayName!!,
            profilePicture = googleAccount.photoUrl.toString())
        return remoteDataSource.login(googleUserModel)
    }

    override suspend fun fetchIntroduction(): NetworkResult<IntroductionResponse> {
        return remoteDataSource.fetchIntroduction(googleAccount.serverAuthCode!!,googleAccount.id!!)
    }

    override suspend fun updateIntroduction(introduction: String): NetworkResult<String> {
        return remoteDataSource.updateIntroduction(googleAccount.serverAuthCode!!, googleAccount.id!!,introduction)
    }
}