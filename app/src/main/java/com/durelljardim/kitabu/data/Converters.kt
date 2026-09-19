package com.durelljardim.kitabu.data

import androidx.room.TypeConverter

// SQLite cannot store an enum, so it goes in as its name string.
class Converters {

    @TypeConverter
    fun fromStatus(status: BookingStatus): String = status.name

    @TypeConverter
    fun toStatus(status: String): BookingStatus = BookingStatus.valueOf(status)
}
