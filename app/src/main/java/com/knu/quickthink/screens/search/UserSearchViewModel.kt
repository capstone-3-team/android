package com.knu.quickthink.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.onErrorOrException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


data class UserInfo(
    val googleName : String = "",
    val googleId : String = "",
    val profilePicture : String = "",
    val profileText : String? = null
)


data class UserSearchUiState(
    val isLoading : Boolean = false,
    val users : List<UserInfo> = emptyList(),
    val isSearchSuccess : Boolean = false,
    val searchUserId : String = "",
    val message : String = ""
)

@HiltViewModel
class UserSearchViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel(){

    private val _uiState  = MutableStateFlow(UserSearchUiState())
    val uiState: StateFlow<UserSearchUiState> = _uiState.asStateFlow()

    fun searchUsers(searchName : String){
        if(searchName.isEmpty()){
            _uiState.update { it.copy(users = emptyList()) }
        }else{
            viewModelScope.launch {
                userRepository.searchUsers(searchName)
                    .onSuccess {
                        _uiState.update { state ->
                            state.copy(users = it.userList)
                        }
                    }.onErrorOrException{ code, message ->
                        Timber.e("updateCard onError : code $code , message $message")
                    }
            }
        }
    }

    fun searchUser(user: UserInfo){
        _uiState.update { it.copy(searchUserId = user.googleName) }
        viewModelScope.launch {
            userRepository.fetchUserInfo(user.googleId)
                .onSuccess {
                    _uiState.update { it.copy(isSearchSuccess = true, searchUserId = user.googleId) }
                }.onErrorOrException{ code, message ->
                    Timber.e("updateCard onError : code $code , message $message")
                    _uiState.update { it.copy(message = "${user.googleName}을 찾을 수 없습니다") }
                }
        }
    }

}