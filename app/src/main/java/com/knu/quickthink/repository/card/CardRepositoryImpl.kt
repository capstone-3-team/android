package com.knu.quickthink.repository.card

import com.knu.quickthink.data.CardRemoteDataSource
import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.card.CreateCardRequest
import com.knu.quickthink.model.card.HashTags
import com.knu.quickthink.model.card.UpdateCardRequest
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.MyCards
import com.knu.quickthink.model.card.otherscard.OthersCard
import com.knu.quickthink.model.card.otherscard.OthersCards
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val remoteDataSource: CardRemoteDataSource
) : CardRepository{

    override suspend fun createCard(createCardRequest: CreateCardRequest): NetworkResult<String> {
        return remoteDataSource.createCard(createCardRequest)
    }

    override suspend fun fetchMyCard(cardId: Long): NetworkResult<MyCard> {
        return remoteDataSource.fetchMyCard(cardId)
    }

    override suspend fun fetchMyCards(hashTags: HashTags): NetworkResult<MyCards> {
        return  remoteDataSource.fetchMyCards(hashTags)
    }

    override suspend fun updateCard(
        cardId: Long,
        updateCardRequest: UpdateCardRequest
    ): NetworkResult<String> {
        return remoteDataSource.updateCard(cardId,updateCardRequest)
    }

    override suspend fun deleteCard(cardId: Long): NetworkResult<String> {
        return remoteDataSource.deleteCard(cardId)
    }

    override suspend fun reviewCard(cardId: Long): NetworkResult<String> {
        return remoteDataSource.reviewCard(cardId)
    }

    override suspend fun fetchOthersCard(cardId: Long): NetworkResult<OthersCard> {
        return remoteDataSource.fetchOthersCard(cardId)
    }

    override suspend fun fetchOthersCards(hashTags: HashTags): NetworkResult<OthersCards> {
        return remoteDataSource.fetchOthersCards(hashTags)
    }
}