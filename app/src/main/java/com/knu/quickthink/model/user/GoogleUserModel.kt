package com.knu.quickthink.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoogleUserModel(
    val token : String,
    val googleId : String,
    val googleName: String,
    val profilePicture: String
) : Parcelable