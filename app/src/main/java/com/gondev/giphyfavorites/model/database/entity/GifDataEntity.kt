package com.gondev.giphyfavorites.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * GIPHY Gif 데이터를 저장하는 데이터베이스 테이블 엔티티입니다
 * 추가로 "좋아요"표시를 할수 있는 favorite필드가 있습니다
 */
@Entity(tableName = "gif_data")
data class GifDataEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val trendingDatetime: Date,
    val thumbnail: String?,
    val thumbnailSize: Int,
    val fixedWidth: String?,
    val fixedWidthSize: Int,
    val originalImage: String?,
    val originalImageSize: Int,
    val originalImageStill: String?,
    val originalImageStillSize: Int,
    val favorite: Boolean = false
)