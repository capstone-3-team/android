package com.knu.quickthink.screens.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.GoogleUserModel
import com.knu.quickthink.model.onError
import com.knu.quickthink.model.onException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GoogleSignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel(){

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLogInSuccess = MutableStateFlow(false)
    val isLogInSuccess: StateFlow<Boolean> = _isLogInSuccess

    private var _userState = MutableStateFlow<GoogleUserModel?>(null)
    val userState: StateFlow<GoogleUserModel?> = _userState

    fun fetchSignInUser(token: String, googleName: String,googleId : String, profilePicture : String) {
        _isLoading.value = true
        Timber.tag("googleLogin").d("fetchSignInUser")
        viewModelScope.launch {
            _userState.value = GoogleUserModel(
                token = token,
                googleName = googleName,
                googleId = googleId,
                profilePicture = profilePicture
            )
            Timber.tag("googleLogin").d("GoogleUserModel : ${_userState.value}")
        }
    }

    fun login(){
        if(_userState.value != null){
            viewModelScope.launch {
                authRepository.login(_userState.value!!)
                    .onSuccess {
                        Timber.tag("googleLogin").d("onSuccess message : $it")
                        _isLogInSuccess.update{true}
                    }
                    .onError { code, message ->
                        Timber.tag("googleLogin").d("onError code : $code, message : $message")
                    }.onException { e ->
                        Timber.tag("googleLogin").d("onException message : ${e.message}")
                    }
            }
        }
        _isLoading.value = false
    }

    fun logOut() {
        _userState.value = null
    }

    fun loginFinished(){
        _isLogInSuccess.value = false
    }

    fun showLoading() {
        _isLoading.value = true
    }
    fun hideLoading() {
        _isLoading.value = false
    }
}