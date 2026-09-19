package com.durelljardim.kitabu.domain

import com.durelljardim.kitabu.data.BookingStatus

// Not an entity. This holds the joined booking and book row for the dashboard.
data class BookingWithBook(
    val bookingId: Int,
    val bookOwnerId: Int,
    val userName: String,
    val bookingDate: Long,
    val returnDeadline: Long,
    val status: BookingStatus,
    val title: String,
    val author: String,
    val category: String
)
