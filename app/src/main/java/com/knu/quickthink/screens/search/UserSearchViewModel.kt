package com.knu.quickthink.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.emptyMyCard
import com.knu.quickthink.model.onErrorOrException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.model.user.UserListResponse
import com.knu.quickthink.model.user.testUserList
import com.knu.quickthink.repository.user.UserRepository
import com.knu.quickthink.screens.card.CardReviewUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


data class UserInfo(
    val googleName : String,
    val googleId : String,
    val profilePicture : String,
    val profileText : String?
)


data class UserSearchUiState(
    val isLoading : Boolean = false,
    val users : List<UserInfo> = emptyList()
)

@HiltViewModel
class UserSearchViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel(){

    private val _uiState  = MutableStateFlow(UserSearchUiState())
    val uiState: StateFlow<UserSearchUiState> = _uiState.asStateFlow()

    fun searchUser(searchName : String){
        if(searchName.isEmpty()){
            _uiState.update { it.copy(users = emptyList()) }
        }else{
            viewModelScope.launch {
                userRepository.searchUser(searchName)
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

}