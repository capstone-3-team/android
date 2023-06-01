package com.knu.quickthink.data

import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.card.*
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.otherscard.OthersCard
import com.knu.quickthink.network.apiService.MyCardApiService
import com.knu.quickthink.network.apiService.OthersCardApiService
import javax.inject.Inject

class CardRemoteDataSource @Inject constructor(
    private val myCardApiService: MyCardApiService,
    private val othersCardApiService: OthersCardApiService
) {
    suspend fun createCard(createCardRequest: CreateCardRequest) : NetworkResult<CreateCardResponse>
        = myCardApiService.createCard(createCardRequest)

    suspend fun fetchMyCard(cardId : Long) : NetworkResult<MyCard>
        = myCardApiService.fetchMyCard(cardId)

    suspend fun fetchMyCards(googleId : String, hashTags: HashTags) : NetworkResult<Cards<MyCard>>
        = myCardApiService.fetchMyCards(googleId,hashTags)

    suspend fun updateCard(
        cardId : Long,
        updateCardRequest: UpdateCardRequest
    ): NetworkResult<String> = myCardApiService.updateCard(cardId,updateCardRequest)

    suspend fun deleteCard(cardId: Long) : NetworkResult<String>
        = myCardApiService.deleteCard(cardId)

    suspend fun reviewCard(cardId : Long) : NetworkResult<String>
        = myCardApiService.reviewCard(cardId)

    suspend fun fetchOthersCard(cardId: Long) : NetworkResult<OthersCard>
        = othersCardApiService.fetchOthersCard(cardId)

    suspend fun fetchOthersCards(hashTags: HashTags) :NetworkResult<Cards<OthersCard>>
        = othersCardApiService.fetchOthersCards(hashTags.hashTags)
}