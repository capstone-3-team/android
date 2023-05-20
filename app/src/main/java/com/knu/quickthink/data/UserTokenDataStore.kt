package com.knu.quickthink.data

import androidx.datastore.core.DataStore
import com.knu.quickthink.UserToken
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class UserTokenDataStore (
    private val userTokenDataStore: DataStore<UserToken>
){
    /**
     * Proto DataStore에 UserToken을 저장함
     */
    suspend fun setUserToken(token: String, googleId: String) {
        Timber.tag("datastore").d("saveUserTokenToDataStore 들어옴")
        userTokenDataStore.updateData { userToken ->
            userToken.toBuilder()
                .setToken(token)
                .setGoogleId(googleId)
                .build()
        }
        Timber.tag("datastore").d("saveUserTokenToDataStore 끝남")
    }

    /**
     * Proto DataStore에 있는 UserToken을 DefaultInstance로 초기화함
     */
    suspend fun initUserToken(){
        userTokenDataStore.updateData {
            UserToken.getDefaultInstance()
        }
    }

    /**
     * Proto DataStore에서 UserToken을 가져옴 / exception catch 수행
     */
    suspend fun getUserToken() : UserToken {
        return userTokenDataStore.data
            .catch { exception ->
                Timber.tag("datastore").d("exception.message : ${exception.message}")
            }.first()
    }
}