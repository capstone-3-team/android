package com.knu.quickthink.model.chatGPT

import com.google.gson.annotations.SerializedName

data class ChatGptResponse(
    val id: String,
    @SerializedName("object")
    val `object`: String,
    val created: Int,
    val model: String,
    val usage: Usage,
    val choices: List<Choice>,
)

data class Usage (
    val prompt_tokens : Int,
    val completion_tokens : Int,
    val total_tokens : Int
)

data class Choice(
    val message : MessageRequest,
    val finish_reason : String,
    val index : Int
)