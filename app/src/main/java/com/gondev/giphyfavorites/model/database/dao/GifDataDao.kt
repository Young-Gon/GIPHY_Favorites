package com.gondev.giphyfavorites.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity

@Dao
interface GifDataDao {
    @Query("SELECT * FROM gif_data ORDER BY trending_datetime DESC LIMIT :pageSize OFFSET :pageIndex")
    fun findGifOrderByTrendingDatetimeDESC(pageSize: Int=20, pageIndex: Int=0): LiveData<List<GifDataEntity>>

    @Query("SELECT * FROM gif_data WHERE favorite=1 ORDER BY trending_datetime DESC LIMIT :pageSize OFFSET :pageIndex")
    fun findFavoriteGifOrderByTrendingDatetimeDESC(pageSize: Int=20, pageIndex: Int=0): LiveData<List<GifDataEntity>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gifDataEntity: List<GifDataEntity>)
}