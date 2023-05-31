package com.knu.quickthink.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.knu.quickthink.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    val googleSignInClient: GoogleSignInClient
): ViewModel(){

    private val _isLogOutSuccess = MutableStateFlow(false)
    val isLogOutSuccess: StateFlow<Boolean> = _isLogOutSuccess.asStateFlow()

    // TODO:  로그인 성공했을 때 isLogOutSuccess를 다시 false로 초기화 해줘야함
    fun logOut() {
        viewModelScope.launch {
            if(userRepository.logOut()){
                _isLogOutSuccess.value = true
            }
            else{
                Timber.tag("googleLogin").d("logout failed")
            }
        }
    }

    fun logOutFinish(){
        _isLogOutSuccess.value = false
    }
}