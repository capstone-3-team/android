package com.knu.quickthink.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber


data class UserState(
    val userToken : String = "",
    val googleId : String = "",
    val isLoggedIn : Boolean = false,
    val message : String = "",
)


class UserManager {
    private val _userState = MutableStateFlow(UserState())
    val userState : StateFlow<UserState> = _userState.asStateFlow()

    fun login(userToken: String, googleId: String){
        _userState.update { state ->
            state.copy(userToken = userToken, googleId = googleId, isLoggedIn = true)
        }
        Timber.d("userToken :${_userState.value.userToken}, isLoggedIn ${_userState.value.isLoggedIn}")
    }
    fun logout() {
        _userState.update { state ->
            state.copy(userToken = "", googleId = "", isLoggedIn = false)
        }
        Timber.d("userToken :${_userState.value.userToken}, isLoggedIn ${_userState.value.isLoggedIn}")
    }

    fun setuserToken(userToken : String){
        _userState.update { state ->
            state.copy(userToken = userToken)
        }
        Timber.d("userToken :${_userState.value.userToken}")
    }

    fun setMessage(message :String){
        _userState.update { state ->
            state.copy(message = message)
        }
        Timber.d("message :${_userState.value.message}")
    }
}