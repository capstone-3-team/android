package com.knu.quickthink.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.knu.quickthink.network.UserManager
import com.knu.quickthink.network.UserState
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
    private val userManager: UserManager,
    private val userRepository: UserRepository
): ViewModel(){

    val userState :StateFlow<UserState> = userManager.userState

    fun logout() {
        viewModelScope.launch {
            if(userRepository.logOut()){
                Timber.d("logout success")
            }
            else{
                Timber.d("logout failed")
            }
        }
    }
}