package com.knu.quickthink.network.apiService

import com.knu.quickthink.model.NetworkResult
import com.knu.quickthink.model.card.mycard.MyCards
import com.knu.quickthink.model.card.otherscard.OthersCard
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OthersCardApiService {

    @POST("/api/card")
    suspend fun fetchOthersCards(
        @Body hashTags :List<String>
    ) : NetworkResult<MyCards>

    @GET("api/card/single")
    suspend fun fetchOthersCard(
        @Query("cardId") cardId : Long
    ) : NetworkResult<OthersCard>


}