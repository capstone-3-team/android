package com.knu.quickthink.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.knu.quickthink.model.onError
import com.knu.quickthink.model.onException
import com.knu.quickthink.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

const val DELAY_TIME = 2500L

@HiltViewModel
class GoogleSignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    val googleSignInClient: GoogleSignInClient
): ViewModel(){

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLogInSuccess = MutableStateFlow(false)
    val isLogInSuccess: StateFlow<Boolean> = _isLogInSuccess.asStateFlow()


    init {
        autoLogin()
    }

    fun login(gsa: GoogleSignInAccount){
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.login(gsa)
                .onError { code, message ->
                    if(code == 200){
                       Timber.tag("googleLogin").d("onError code : $code -> success")
                        delay(DELAY_TIME)
                        _isLogInSuccess.value = true
                    }else{
                       Timber.tag("googleLogin").d("onError code : $code, message : $message")

                    }
                }.onException { e ->
                    Timber.tag("googleLogin").d("onException message : ${e.message}")
                }
        }
    }

    fun autoLogin(){
        viewModelScope.launch {
            _isLoading.value = true
            Timber.tag("datastore").d("autoLogin() start")
            val autoLoginSuccess = userRepository.autoLogin()
            Timber.tag("datastore").d("autoLogin() $autoLoginSuccess")
            if(autoLoginSuccess){
                delay(DELAY_TIME)
                _isLogInSuccess.value = true
                Timber.tag("datastore").d("autoLogin true")
//               _isLoading.value = false
           }else{
                Timber.tag("datastore").d("autoLogin false")
                _isLoading.value = false
            }
        }
    }

}