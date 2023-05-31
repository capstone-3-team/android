package com.knu.quickthink.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.onError
import com.knu.quickthink.model.onException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository : UserRepository
): ViewModel(){

    private val _profileImage = MutableStateFlow("https://lh3.googleusercontent.com/a/AGNmyxbXgisKVPcdOHLgTDqJTbyFz7r7QmkTchH1t9VS=s96-c")
    val profileImage :StateFlow<String> = _profileImage.asStateFlow()

    private val _googleId = MutableStateFlow("Google ID")
    val googleId :StateFlow<String> = _googleId.asStateFlow()

    private val _introduction = MutableStateFlow("자기소개 내용을 입력하세요")
    val introduction : StateFlow<String> = _introduction.asStateFlow()

    private val _isUpdateSuccess = MutableStateFlow(false)
    val isUpdateSuccess: StateFlow<Boolean> = _isUpdateSuccess.asStateFlow()

    init {
        viewModelScope.launch{
            repository.fetchIntroduction()
                .onSuccess {
                    _introduction.value = it.text
                }.onError { code, message ->
                    Timber.tag("account").d("onError code : $code, msg : $message")
                }.onException {
                    Timber.tag("account").d("onException msg : ${it.message}")
                }
        }
    }

    fun editIntroduction(newIntroduction : String){
        _introduction.value = newIntroduction
    }

    fun updateIntroduction(){
        viewModelScope.launch {
            repository.updateIntroduction(introduction.value)
                .onSuccess {
                    _isUpdateSuccess.value = true
                }.onError { code, message ->
                    if(code == 200){
                        _isUpdateSuccess.value = true
                    }
                    Timber.tag("account").d("onError code : $code, msg : $message")
                }.onException {
                    Timber.tag("account").d("onException msg : ${it.message}")
                }
        }
    }
}