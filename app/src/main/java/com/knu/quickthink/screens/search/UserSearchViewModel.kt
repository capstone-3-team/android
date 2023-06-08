package com.knu.quickthink.screens.search

import androidx.lifecycle.ViewModel
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.emptyMyCard
import com.knu.quickthink.screens.card.CardReviewUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


data class UserInfo(
    val googleName : String,
    val googleId : String,
    val profilePicture : String,
    val profileText : String
)

val testUserInfoList= listOf(UserInfo("abc","googleId","","프로필 내용"))

data class UserSearchUiState(
    val isLoading : Boolean = false,
    val users : List<UserInfo> = testUserInfoList
)

@HiltViewModel
class UserSearchViewModel @Inject constructor(
): ViewModel(){

    private val _uiState  = MutableStateFlow(UserSearchUiState())
    val uiState: StateFlow<UserSearchUiState> = _uiState.asStateFlow()

}