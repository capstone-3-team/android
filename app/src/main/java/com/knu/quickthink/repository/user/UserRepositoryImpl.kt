package com.knu.quickthink.repository.user

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import com.knu.quickthink.model.user.UserListResponse
import com.knu.quickthink.network.RetrofitFailureStateException
import com.knu.quickthink.network.UserManager
import com.knu.quickthink.screens.search.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject

const val DEFAULT_PROFILE_IMAGE =
    "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"

data class User(
    val token :String = "",
    val googleId :String = ""
)

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val googleSignInClient: GoogleSignInClient,
    private val userTokenDataStore: UserTokenDataStore,
    private val userManager: UserManager
) : UserRepository {
    override suspend fun login(gsa: GoogleSignInAccount): Result<String> {

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
        return when(val networkResult = remoteDataSource.login(googleUserModel)){
            is NetworkResult.Success -> {
                userManager.login(userToken = gsa.serverAuthCode!!,gsa.id!!)
                Result.success("success")
            }
            is NetworkResult.Error -> {
                if(networkResult.code == 200) {
                    userManager.login(userToken = gsa.serverAuthCode!!,gsa.id!!)
                    Result.success("success")
                }
                else{
                    Result.failure(
                        RetrofitFailureStateException(networkResult.message,networkResult.code)
                    )
                }
            }
            is NetworkResult.Exception ->{
                Result.failure(
                    RetrofitFailureStateException(networkResult.e.message,999)
                )
            }

        }
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

    override suspend fun searchUser(searchName: String): NetworkResult<UserListResponse> =
        remoteDataSource.searchUser(searchName)

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
                    userManager.login(userToken = refreshedToken, googleId = userToken.googleId)
                    isLoggedIn = true
                    Timber.tag("googleLogin").d("refreshedToken is valid")
                } else {
                    userTokenDataStore.initUserToken()
                    isLoggedIn = false
                    Timber.tag("googleLogin").d("refreshedToken is null or not valid")
                }
            } else {
                Timber.tag("datastore").d("token valid")
                userManager.login(userToken = userToken.token, googleId = userToken.googleId)
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
        if(isLoggedOut) userManager.logout()
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

