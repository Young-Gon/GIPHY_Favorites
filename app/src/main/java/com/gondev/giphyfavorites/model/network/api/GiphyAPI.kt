package com.gondev.giphyfavorites.model.network.api

import com.gondev.giphyfavorites.model.network.response.Result
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * GIPHY 서버로 부터 데이터를 받아옵니다
 */
interface GiphyAPI {
    @GET("trending")
    suspend fun getGifList(@Query("limit") limit: Int=20, @Query("offset") offset: Int=0): Result
}