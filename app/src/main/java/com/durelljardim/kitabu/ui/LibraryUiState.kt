package com.durelljardim.kitabu.ui

import com.durelljardim.kitabu.data.BookEntity
import com.durelljardim.kitabu.domain.BookingWithBook

data class LibraryUiState(
    val books: List<BookEntity> = emptyList(),
    val bookings: List<BookingWithBook> = emptyList(),
    val searchQuery: String = "",
    val availableOnly: Boolean = false,
    val message: String? = null
)
