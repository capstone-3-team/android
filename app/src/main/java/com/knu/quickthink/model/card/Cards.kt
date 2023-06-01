package com.knu.quickthink.model.card

import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.dummyMyCard

data class Cards<T>(
    val size : Long,
    val isYours : Boolean,
    val cards : List<T>
)

val emptyMyCards = Cards<MyCard>(
    size = 0 ,
    isYours = true,
    cards = emptyList()
)

val dummyMyCards = Cards(
    size = 3,
    isYours = true,
    cards = listOf(dummyMyCard, dummyMyCard, dummyMyCard)
)