package com.gondev.giphyfavorites.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gondev.giphyfavorites.model.database.converter.TimeConverter
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity

/**
 * 데이터베이스 모듈입니다
 * 모든 entity와 dao를 관리하고
 * 마이그레이션을 진행 합니다
 */
@Database(
    entities = [
        GifDataEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getGifDataDao(): GifDataDao
}