package com.knu.quickthink.model.card.mycard

import java.time.LocalDateTime

data class MyCard(
    val id : Long,
    val title : String,
    val isYours : Boolean,
    val content : String,
    val hasTags : HashSet<String>,
    val writtenDate : LocalDateTime,
    val latestReviewDate : LocalDateTime,
    val reviewCount : Long
)