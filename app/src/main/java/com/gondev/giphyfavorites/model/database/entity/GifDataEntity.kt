package com.gondev.giphyfavorites.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "gif_data")
data class GifDataEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val trendingDatetime: Date,
    val thumbnail: String?,
    val thumbnailSize: Int,
    val fixedWidthDownsampled: String?,
    val fixedWidthDownsampledSize: Int,
    val originalImage: String?,
    val originalImageSize: Int,
    val favorite: Boolean = false
)