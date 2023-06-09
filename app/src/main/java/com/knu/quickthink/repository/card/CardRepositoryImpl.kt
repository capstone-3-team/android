package com.knu.quickthink.repository.card

import com.knu.quickthink.data.CardRemoteDataSource
import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.card.*
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.otherscard.OthersCard
import com.knu.quickthink.network.UserManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val remoteDataSource: CardRemoteDataSource,
    private val userManager: UserManager
) : CardRepository{

    override suspend fun createCard(createCardRequest: CreateCardRequest): NetworkResult<CreateCardResponse> {
        return remoteDataSource.createCard(createCardRequest)
    }

    override suspend fun fetchMyCard(cardId: Long): NetworkResult<MyCard> {
        return remoteDataSource.fetchMyCard(cardId)
    }
    override suspend fun fetchOthersCard(cardId: Long): NetworkResult<OthersCard> {
        return remoteDataSource.fetchOthersCard(cardId)
    }

    override suspend fun fetchMyCards(hashTags: HashTags): NetworkResult<Cards<MyCard>> {
        val user = userManager.userState.first()
        return  remoteDataSource.fetchMyCards(user.googleId,hashTags)
    }
    override suspend fun fetchOthersCards(googleId: String, hashTags: HashTags): NetworkResult<Cards<OthersCard>> {
        return remoteDataSource.fetchOthersCards(googleId,hashTags)
    }

    override suspend fun fetchHashTags(googleId : String?): NetworkResult<HashTags> {
        val user = userManager.userState.first()
        return   remoteDataSource.fetchHashTags(googleId ?:user.googleId)
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

}