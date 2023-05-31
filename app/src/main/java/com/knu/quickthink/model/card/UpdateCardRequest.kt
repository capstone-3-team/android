package com.knu.quickthink.model.card

import java.time.LocalDateTime

data class UpdateCardRequest(
    val title : String,
    val content : String,
    val hashTags: HashTags,
    val writtenDate : LocalDateTime
)
