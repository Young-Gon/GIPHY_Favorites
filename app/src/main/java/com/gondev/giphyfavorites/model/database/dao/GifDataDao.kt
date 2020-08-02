package com.gondev.giphyfavorites.model.database.dao

import androidx.paging.DataSource
import androidx.room.*
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity

@Dao
interface GifDataDao {
    @Query("SELECT * FROM gif_data ORDER BY trendingDatetime DESC")
    fun findGifOrderByTrendingDatetimeDESC(): DataSource.Factory<Int, GifDataEntity>

    @Query("SELECT * FROM gif_data WHERE favorite=1 ORDER BY trendingDatetime DESC")
    fun findFavoriteGifOrderByTrendingDatetimeDESC(): DataSource.Factory<Int, GifDataEntity>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gifDataEntity: List<GifDataEntity>)
}