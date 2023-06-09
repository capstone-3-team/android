package com.knu.quickthink.model.card

data class HashTags(
    val hashTags: List<String>
)

data class HashTagItem(
    val value : String,
    val isSelected : Boolean
)
val dummyHashTags = HashTags(
    hashTags = listOf()
)