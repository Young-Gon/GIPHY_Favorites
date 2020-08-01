package com.gondev.giphyfavorites.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity

@Database(
    entities = [
        GifDataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getGifDataDao(): GifDataDao
}