package com.knu.quickthink.model.converter

import com.knu.quickthink.model.card.Cards
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.utils.convertDateFormat


fun convertMyCardsDate(cards : Cards<MyCard>) : Cards<MyCard> {
    return cards.copy(
        cards = cards.cards.map {
            it.copy(
                writtenDate = convertDateFormat(it.writtenDate),
                latestReviewDate = convertDateFormat(it.latestReviewDate)
            )
        }
    )
}

fun convertMyCardDate(card :MyCard) : MyCard {
    return card.copy(
        writtenDate = convertDateFormat(card.writtenDate),
        latestReviewDate = convertDateFormat(card.latestReviewDate)
    )
}