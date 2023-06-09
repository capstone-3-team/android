package com.knu.quickthink.utils

import java.net.URL

fun String.isURL(): Boolean {
    return try {
        URL(this)
        true
    } catch (e: Exception) {
        false
    }
}
