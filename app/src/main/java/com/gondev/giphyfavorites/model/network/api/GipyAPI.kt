package com.gondev.giphyfavorites.model.network.api

import com.gondev.giphyfavorites.model.network.response.GifData
import retrofit2.http.GET
import retrofit2.http.Query

interface GipyAPI {
    @GET("trending")
    suspend fun getMovieList(@Query("limit") limit: Int=20,@Query("offset") offset: Int=0): List<GifData>
}