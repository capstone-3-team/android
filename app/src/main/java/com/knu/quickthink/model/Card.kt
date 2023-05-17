package com.knu.quickthink.model

data class Card(
    val id: String="",
    val title: String = "",
    val content: String = "",
    val hashTags : List<String> = listOf(),
    val reviewCount : Int = 0,
    val userId : Int = 0,
    val isCompleted: Boolean = false,
    val writtenDate : String = "2023-04-23T07:40:58.008Z",
    val latestReviewDate : String = "2023-04-23T07:40:58.008Z",
)