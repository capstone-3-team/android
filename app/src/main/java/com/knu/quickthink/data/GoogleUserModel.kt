package com.knu.quickthink.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoogleUserModel(
    val token : String,
    val googleId : String,
    val googleName: String,
    val profilePicture: String
) : Parcelable