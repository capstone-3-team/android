package com.knu.quickthink.repository.card

import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.card.*
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.otherscard.OthersCard

interface CardRepository {

    suspend fun createCard(createCardRequest: CreateCardRequest) : NetworkResult<String>

    suspend fun fetchMyCard(cardId : Long) : NetworkResult<MyCard>

    suspend fun fetchMyCards(hashTags: HashTags) : NetworkResult<Cards<MyCard>>

    suspend fun updateCard( cardId : Long, updateCardRequest: UpdateCardRequest): NetworkResult<String>

    suspend fun deleteCard(cardId: Long) : NetworkResult<String>

    suspend fun reviewCard(cardId : Long) : NetworkResult<String>

    suspend fun fetchOthersCard(cardId: Long) : NetworkResult<OthersCard>

    suspend fun fetchOthersCards(hashTags: HashTags) :NetworkResult<Cards<OthersCard>>
}