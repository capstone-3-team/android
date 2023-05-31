package com.knu.quickthink.model.card

import java.time.LocalDateTime

data class CreateCardRequest(
    val title : String,
    val hashTags : HashTags,
    val writtenDate : LocalDateTime,
    val latestReviewDate : LocalDateTime,
    val reviewCount : Long
)
