package com.knu.quickthink.model.card.otherscard

import java.time.LocalDateTime

data class OthersCard(
    val id : Long,
    val title : String,
    val isYours : Boolean,
    val content : String,
    val hasTags : HashSet<String>,
    val writtenDate : LocalDateTime,
    val googleId : String
)
