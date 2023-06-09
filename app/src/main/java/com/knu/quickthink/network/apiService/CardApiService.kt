package com.knu.quickthink.network.apiService

import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.card.*
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.otherscard.OthersCard
import retrofit2.http.*

interface CardApiService {

    @POST("api/card/write")
    suspend fun createCard(
        @Body createCardRequest : CreateCardRequest
    ): NetworkResult<CreateCardResponse>

    @GET("api/card/single")
    suspend fun fetchMyCard(
        @Query("cardId") cardId : Long
    ) : NetworkResult<MyCard>
    @GET("api/card/single")
    suspend fun fetchOthersCard(
        @Query("cardId") cardId : Long
    ) : NetworkResult<OthersCard>

    @POST("/api/card")
    suspend fun fetchMyCards(
        @Query("googleId") googleId : String,
        @Body hashTags : HashTags
    ) : NetworkResult<Cards<MyCard>>
    @POST("/api/card")
    suspend fun fetchOthersCards(
        @Query("googleId") googleId : String,
        @Body hashTags : HashTags
    ) : NetworkResult<Cards<OthersCard>>


    @GET("/api/hashtags")
    suspend fun fetchHashTags(
        @Query("googleId") googleId : String,
    ) :NetworkResult<HashTags>


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