package com.knu.quickthink.utils

import timber.log.Timber

class CustomTimberTree: Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "${element.className}_${element.methodName}"
    }
}