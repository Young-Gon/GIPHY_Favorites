package com.gondev.giphyfavorites.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "gif_data")
data class GifDataEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val trending_datetime: Date,
    val previewWebp: String,
    val fixedWidthDownsampled: String,
    val originalImage: String,
    val favorite: Boolean = false
)