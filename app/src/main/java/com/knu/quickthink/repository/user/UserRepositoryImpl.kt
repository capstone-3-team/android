package com.knu.quickthink.repository.user

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.knu.quickthink.UserToken
import com.knu.quickthink.data.UserRemoteDataSource
import com.knu.quickthink.data.UserTokenDataStore
import com.knu.quickthink.model.*
import com.knu.quickthink.model.user.GoogleUserModel
import com.knu.quickthink.model.user.IntroductionResponse
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject

const val DEFAULT_PROFILE_IMAGE =
    "https://img.freepik.com/premium-vector/account-icon-user-icon-vector-graphics_292645-552.jpg?w=996"

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val googleSignInClient: GoogleSignInClient,
    private val userTokenDataStore: UserTokenDataStore
) : UserRepository {
    override suspend fun login(gsa: GoogleSignInAccount): NetworkResult<String> {

        if (gsa.serverAuthCode == null || gsa.displayName == null || gsa.id == null) {
            throw NullPointerException("GoogleSignInAccount Data null")
        }
        val googleUserModel = GoogleUserModel(
            token = gsa.serverAuthCode!!,
            googleId = gsa.id!!,
            googleName = gsa.displayName!!,
            profilePicture = gsa.photoUrl?.toString() ?: DEFAULT_PROFILE_IMAGE
        )

        userTokenDataStore.setUserToken(gsa.serverAuthCode!!, gsa.id!!)
        return remoteDataSource.login(googleUserModel)
    }

    override suspend fun fetchIntroduction(): NetworkResult<IntroductionResponse> {
        val userToken = userTokenDataStore.getUserToken()
        return remoteDataSource.fetchIntroduction(userToken.token, userToken.googleId)
    }

    override suspend fun updateIntroduction(introduction: String): NetworkResult<String> {
        val userToken = userTokenDataStore.getUserToken()
        return remoteDataSource.updateIntroduction(
            userToken.token,
            userToken.googleId,
            introduction
        )
    }

    override suspend fun autoLogin(): Boolean {
        var isLoggedIn = true
        val userToken = userTokenDataStore.getUserToken()
        Timber.tag("datastore")
            .d("fetch UserToken token : ${userToken.token}, googleId : ${userToken.googleId}")
        if (userToken == UserToken.getDefaultInstance()) {
            isLoggedIn = false
        } else {
            if (!accessTokenValidCheck(userToken.token, userToken.googleId)) {
                Timber.tag("googleLogin").d("refresh Try")
                val refreshedToken = refreshToken()
                if (refreshedToken.isNotEmpty() && updateRefreshToken(refreshedToken,userToken.googleId)) {
                    userTokenDataStore.setUserToken(refreshedToken, userToken.googleId)
                    isLoggedIn = true
                    Timber.tag("googleLogin").d("refreshedToken is valid")
                } else {
                    userTokenDataStore.initUserToken()
                    isLoggedIn = false
                    Timber.tag("googleLogin").d("refreshedToken is null or not valid")
                }
            } else {
                Timber.tag("datastore").d("token valid")
                isLoggedIn = true
            }
        }
        Timber.tag("datastore").d("autoLogin  isLoggedIn : $isLoggedIn")
        return isLoggedIn
    }

    override suspend fun logOut(): Boolean {
        var isLoggedOut = false
        googleSignInClient.signOut().addOnCompleteListener {
            Timber.tag("googleLogin").d("logOut Success")
            isLoggedOut = true
        }
        try {
            userTokenDataStore.initUserToken()
            isLoggedOut = true
        } catch (e: Exception) {
            Timber.tag("googleLogin").e("logOut Error Exception : ${e.message}")
        }
        return isLoggedOut
    }

    private suspend fun accessTokenValidCheck(token: String, googleId: String): Boolean {
        Timber.tag("googleLogin").d("tokenValidCheck Start")
        return when (val response = remoteDataSource.tokenValidCheck(token, googleId)) {
            is NetworkResult.Success -> {
                response.data.isValid
            }
            is NetworkResult.Error -> false
            is NetworkResult.Exception -> false
        }
    }

    private suspend fun refreshToken(): String {
        /* refresh 로직 */
        Timber.tag("googleLogin").d("start refresh")
        var token = ""
        try {
            val account = googleSignInClient.silentSignIn()
            val signInResult = withTimeoutOrNull(3000) {
                account.await()
            } ?: throw ApiException(Status.RESULT_TIMEOUT)
            token = signInResult.serverAuthCode
                ?: throw NoSuchElementException("there is no serverAuthCode in GooleSingInAccount")
            Timber.tag("googleLogin").d("success refresh $token")
        } catch (e: ApiException) {
            Timber.tag("googleLogin").e("refreshToken ApiExecption")
        } catch (e: NoSuchElementException) {
            Timber.tag("googleLogin").e(e)
        }
        return token
    }

    private suspend fun updateRefreshToken(token: String, googleId: String): Boolean {
        return when (val response = remoteDataSource.updateToken(token, googleId)) {
            is NetworkResult.Success -> true
            is NetworkResult.Error -> response.code == 200
            is NetworkResult.Exception -> false
        }
    }
}

