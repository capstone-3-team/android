package com.knu.quickthink.data

import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.card.*
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.otherscard.OthersCard
import com.knu.quickthink.network.apiService.CardApiService
import javax.inject.Inject

class CardRemoteDataSource @Inject constructor(
    private val cardApiService: CardApiService,
) {
    suspend fun createCard(createCardRequest: CreateCardRequest) : NetworkResult<CreateCardResponse>
        = cardApiService.createCard(createCardRequest)

    suspend fun fetchMyCard(cardId : Long) : NetworkResult<MyCard>
        = cardApiService.fetchMyCard(cardId)
    suspend fun fetchOthersCard(cardId: Long) : NetworkResult<OthersCard>
        = cardApiService.fetchOthersCard(cardId)

    suspend fun fetchMyCards(googleId : String, hashTags: HashTags) : NetworkResult<Cards<MyCard>>
        = cardApiService.fetchMyCards(googleId,hashTags)
    suspend fun fetchOthersCards(googleId : String, hashTags: HashTags) :NetworkResult<Cards<OthersCard>>
        = cardApiService.fetchOthersCards(googleId,hashTags)

    suspend fun fetchHashTags(googleId: String): NetworkResult<HashTags>
        = cardApiService.fetchHashTags(googleId)


    suspend fun updateCard(
        cardId : Long,
        updateCardRequest: UpdateCardRequest
    ): NetworkResult<String> = cardApiService.updateCard(cardId,updateCardRequest)

    suspend fun deleteCard(cardId: Long) : NetworkResult<String>
        = cardApiService.deleteCard(cardId)

    suspend fun reviewCard(cardId : Long) : NetworkResult<String>
        = cardApiService.reviewCard(cardId)


}