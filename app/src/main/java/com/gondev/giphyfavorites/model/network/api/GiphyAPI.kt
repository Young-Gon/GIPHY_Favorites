package com.gondev.giphyfavorites.model.network.api

import com.gondev.giphyfavorites.model.network.response.Result
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyAPI {
    @GET("trending")
    suspend fun getGifList(@Query("limit") limit: Int=20, @Query("offset") offset: Int=0): Result
}