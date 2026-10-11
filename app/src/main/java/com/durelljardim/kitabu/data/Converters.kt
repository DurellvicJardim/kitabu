package com.durelljardim.kitabu.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromStatus(status: BookingStatus): String = status.name

    @TypeConverter
    fun toStatus(status: String): BookingStatus = BookingStatus.valueOf(status)
}
