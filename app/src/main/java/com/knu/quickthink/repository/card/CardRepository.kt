package com.knu.quickthink.repository.card

import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.card.CreateCardRequest
import com.knu.quickthink.model.card.HashTags
import com.knu.quickthink.model.card.UpdateCardRequest
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.MyCards
import com.knu.quickthink.model.card.otherscard.OthersCard
import com.knu.quickthink.model.card.otherscard.OthersCards

interface CardRepository {

    suspend fun createCard(createCardRequest: CreateCardRequest) : NetworkResult<String>

    suspend fun fetchMyCard(cardId : Long) : NetworkResult<MyCard>

    suspend fun fetchMyCards(hashTags: HashTags) : NetworkResult<MyCards>

    suspend fun updateCard( cardId : Long, updateCardRequest: UpdateCardRequest): NetworkResult<String>

    suspend fun deleteCard(cardId: Long) : NetworkResult<String>

    suspend fun reviewCard(cardId : Long) : NetworkResult<String>

    suspend fun fetchOthersCard(cardId: Long) : NetworkResult<OthersCard>

    suspend fun fetchOthersCards(hashTags: HashTags) :NetworkResult<OthersCards>
}