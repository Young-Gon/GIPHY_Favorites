package com.gondev.giphyfavorites.model.database.converter

import androidx.room.TypeConverter
import java.util.*

class TimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long) = Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date?) = date?.time?:0

}
