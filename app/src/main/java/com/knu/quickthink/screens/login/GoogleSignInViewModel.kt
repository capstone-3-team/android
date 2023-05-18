package com.knu.quickthink.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.knu.quickthink.model.GoogleUserModel
import com.knu.quickthink.model.onError
import com.knu.quickthink.model.onException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GoogleSignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    val googleSignInClient: GoogleSignInClient
): ViewModel(){

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLogInSuccess = MutableStateFlow(false)
    val isLogInSuccess: StateFlow<Boolean> = _isLogInSuccess.asStateFlow()

    private var _userState = MutableStateFlow<GoogleUserModel?>(null)
    val userState: StateFlow<GoogleUserModel?> = _userState.asStateFlow()

    fun login(gsa: GoogleSignInAccount){
//        if(_userState.value != null){
            viewModelScope.launch {
                _isLoading.value = true
                userRepository.login(gsa)
                    .onSuccess {
                        Timber.tag("googleLogin").d("onSuccess message : $it")
                        _isLogInSuccess.value = true
                    }
                    .onError { code, message ->
                        if(code == 200){
                           Timber.tag("googleLogin").d("onError code : $code -> success")
//                            delay(3200L)
                            _isLogInSuccess.value = true
                        }else{
                           Timber.tag("googleLogin").d("onError code : $code, message : $message")

                        }
                    }.onException { e ->
                        Timber.tag("googleLogin").d("onException message : ${e.message}")
                    }
            }
//        }
    }
    fun logOut() {
        _userState.value = null
    }
}