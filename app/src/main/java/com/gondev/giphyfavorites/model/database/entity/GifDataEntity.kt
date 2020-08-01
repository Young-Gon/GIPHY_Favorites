package com.gondev.giphyfavorites.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GifDataEntity (
    @PrimaryKey
    val id: String
)