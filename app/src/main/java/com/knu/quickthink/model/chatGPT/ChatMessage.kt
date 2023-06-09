package com.knu.quickthink.model.chatGPT

data class ChatMessage(
    val role : String = "user",
    val content : String = ""
)
