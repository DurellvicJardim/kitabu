package com.durelljardim.kitabu.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Deleting a book also deletes its bookings.
@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookId"],
            childColumns = ["bookOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Without this index, joining a booking to its book means a full table scan.
    indices = [Index("bookOwnerId")]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val bookingId: Int = 0,
    val bookOwnerId: Int,
    val userName: String,
    val bookingDate: Long,
    val returnDeadline: Long,
    val status: BookingStatus
)
