package com.knu.quickthink.model.card

import java.time.LocalDateTime

data class CreateCardRequest(
    val title : String = "제목",
    val content : String = "카드 내용을 입력해주세요",
    val hashTags : List<String> = emptyList(),
    val writtenDate : String = LocalDateTime.now().toString(),
    val latestReviewDate : String= LocalDateTime.now().toString(),
    val reviewCount : Long = 0L
)
