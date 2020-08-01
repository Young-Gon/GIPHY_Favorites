package com.gondev.giphyfavorites.model.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity

@Dao
interface GifDataDao {
    @Query("SELECT * FROM gif_data ORDER BY trending_datetime DESC")
    fun findGifOrderByTrendingDatetimeDESC(): DataSource.Factory<Int, GifDataEntity>

    @Query("SELECT * FROM gif_data WHERE favorite=1 ORDER BY trending_datetime DESC")
    fun findFavoriteGifOrderByTrendingDatetimeDESC(): DataSource.Factory<Int, GifDataEntity>
}