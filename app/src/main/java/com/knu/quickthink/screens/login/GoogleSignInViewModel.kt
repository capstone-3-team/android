package com.knu.quickthink.screens.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.knu.quickthink.model.GoogleUserModel
import com.knu.quickthink.model.onError
import com.knu.quickthink.model.onException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GoogleSignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val googleSignInClient: GoogleSignInClient
): ViewModel(){

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLogInSuccess = MutableStateFlow(false)
    val isLogInSuccess: StateFlow<Boolean> = _isLogInSuccess.asStateFlow()

    private var _userState = MutableStateFlow<GoogleUserModel?>(null)
    val userState: StateFlow<GoogleUserModel?> = _userState.asStateFlow()

    fun fetchSignInUser(token: String, googleName: String,googleId : String, profilePicture : String) {
        Timber.tag("googleLogin").d("fetchSignInUser")
        viewModelScope.launch {
            _isLoading.value = true
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
                        _isLogInSuccess.value = true
                    }
                    .onError { code, message ->
                        if(code == 200){
                           Timber.tag("googleLogin").d("onError code : $code -> success")
                            delay(3200L)
                            _isLogInSuccess.value = true
                        }else{
                           Timber.tag("googleLogin").d("onError code : $code, message : $message")

                        }
                    }.onException { e ->
                        Timber.tag("googleLogin").d("onException message : ${e.message}")
                    }
            }
        }
    }
    fun logOut() {
        _userState.value = null
    }
}