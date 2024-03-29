package com.knu.quickthink.model.chatGPT

data class ChatGptRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<MessageRequest> = emptyList(),
    val temperature: Double = 0.7
)

