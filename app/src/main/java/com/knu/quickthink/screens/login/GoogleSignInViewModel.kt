package com.knu.quickthink.screens.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.data.GoogleUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GoogleSignInViewModel @Inject constructor(application: Application): ViewModel(){

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
        if(_userState.value != null){
            _isLogInSuccess.value = true
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