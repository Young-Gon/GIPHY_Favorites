package com.gondev.giphyfavorites.model.database.dao

import androidx.paging.DataSource
import androidx.room.*
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import java.util.*

/**
 * GIPHY로 부터 받은 데이터를 디비에 읽고 쓰는 클레스입니다
 */
@Dao
interface GifDataDao {

    /**
     * 서버로 부터 받은 Gif 데이터를 최신순으로 찾습니다
     * @return pageing에서 사용하는 DataSource형태로 넘깁니다
     */
    @Query("SELECT * FROM gif_data ORDER BY trendingDatetime DESC")
    fun findGif(): DataSource.Factory<Int, GifDataEntity>

    @Query("SELECT trendingDatetime FROM gif_data ORDER BY trendingDatetime DESC")
    suspend fun findFirstGifDate(): Date?

    /**
     * Gif 데이터 중 "좋아요" 표시를 한 데이터만 최신순으로 찾습니다
     * @return pageing에서 사용하는 DataSource형태로 넘깁니다
     */
    @Query("SELECT * FROM gif_data WHERE favorite=1 ORDER BY trendingDatetime DESC")
    fun findFavoriteGif(): DataSource.Factory<Int, GifDataEntity>

    /**
     * 서버로 부터 받은 데이터를 추가합니다
     * 만약 이미 추가된 아이템이 있으면 무시합니다
     * 무시하는 이유는 리스트의 불필요한 화면갱신을 막기 위함입니다
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(gifDataEntity: List<GifDataEntity>)

    /**
     * 데이터를 업데이트 합니다
     * "좋아요"표시를 하기 위해 사용 합니다
     */
    @Update
    suspend fun update(entity: GifDataEntity)
}