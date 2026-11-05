package io.github.carvalhoxx.data.local

import androidx.room.TypeConverter

class Converters {
    @Suppress("unused")
    @TypeConverter
    fun fromLapList(laps: List<Long>): String {
        return laps.joinToString(separator = ",")
    }

    @Suppress("unused")
    @TypeConverter
    fun toLapList(data: String): List<Long> {
        if (data.isBlank()) return emptyList()
        return data.split(",").mapNotNull { it.toLongOrNull() }
    }
}
