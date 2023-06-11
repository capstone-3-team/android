package com.knu.quickthink.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.onError
import com.knu.quickthink.model.onException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.repository.user.UserRepository
import com.knu.quickthink.screens.search.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class AccountUiState(
    val isLoading : Boolean = false,
    val userInfo :UserInfo = UserInfo(),
    val isUpdateSuccess : Boolean = false,
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository : UserRepository
): ViewModel(){

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState : StateFlow<AccountUiState> = _uiState.asStateFlow()
    init {
        viewModelScope.launch{
            repository.fetchUserInfo(null)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(userInfo = it)
                    }
                }.onError { code, message ->
                    Timber.tag("account").d("onError code : $code, msg : $message")
                }.onException {
                    Timber.tag("account").d("onException msg : ${it.message}")
                }
        }
    }

    fun editIntroduction(newIntroduction : String){
        _uiState.update { state ->
            state.copy(userInfo = state.userInfo.copy(profileText = newIntroduction))
        }
    }

    fun updateIntroduction(){
        viewModelScope.launch {
            repository.updateIntroduction(uiState.value.userInfo.profileText ?: "")
                .onSuccess {
                    _uiState.update {it.copy(isUpdateSuccess = true) }
                }.onError { code, message ->
                    Timber.tag("account").d("onError code : $code, msg : $message")
                }.onException {
                    Timber.tag("account").d("onException msg : ${it.message}")
                }
        }
    }
}