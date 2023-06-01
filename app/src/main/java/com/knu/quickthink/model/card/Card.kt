package com.knu.quickthink.model.card

import java.time.LocalDateTime

open class Card(
    open val id: Long,
    open val title: String,
    open val content: String,
    open val isYours : Boolean,
    open val hashTags : HashSet<String>,
    open val writtenDate : LocalDateTime,
)

