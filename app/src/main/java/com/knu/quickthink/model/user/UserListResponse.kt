package com.knu.quickthink.model.user

import com.knu.quickthink.repository.user.DEFAULT_PROFILE_IMAGE
import com.knu.quickthink.screens.search.UserInfo

data class UserListResponse(
    val userList : List<UserInfo>
)

val testUserList= UserListResponse(List(40){
    UserInfo("abc$it","googleId",DEFAULT_PROFILE_IMAGE,"프로필 내용")
})
