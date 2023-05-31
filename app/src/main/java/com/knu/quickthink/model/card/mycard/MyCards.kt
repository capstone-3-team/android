package com.knu.quickthink.model.card.mycard

data class MyCards(
    val size : Long,
    val isYours : Boolean,
    val cards : List<MyCard>
)