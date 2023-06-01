package com.knu.quickthink.network.apiService

import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.card.Cards
import com.knu.quickthink.model.card.CreateCardRequest
import com.knu.quickthink.model.card.HashTags
import com.knu.quickthink.model.card.UpdateCardRequest
import com.knu.quickthink.model.card.mycard.MyCard
import retrofit2.http.*

interface MyCardApiService {

    @POST("api/card/write")
    suspend fun createCard(
        @Body createCardRequest : CreateCardRequest
    ): NetworkResult<String>

    @GET("api/card/single")
    suspend fun fetchMyCard(
        @Query("cardId") cardId : Long
    ) : NetworkResult<MyCard>

    @POST("/api/card")
    suspend fun fetchMyCards(
        @Query("googleId") googleId : String,
        @Body hashTags : HashTags
    ) : NetworkResult<Cards<MyCard>>

    @PUT("api/card/edit")
    suspend fun updateCard(
        @Query("cardId") cardId : Long,
        @Body updateCardRequest: UpdateCardRequest
    ) : NetworkResult<String>

    @DELETE("api/card/remove")
    suspend fun deleteCard(
        @Query("cardId") cardId : Long
    ): NetworkResult<String>

    @POST("api/card/review")
    suspend fun reviewCard(
        @Query("cardId") cardId : Long
    ): NetworkResult<String>
}