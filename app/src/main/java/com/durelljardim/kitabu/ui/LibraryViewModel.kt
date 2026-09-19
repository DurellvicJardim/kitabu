package com.durelljardim.kitabu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durelljardim.kitabu.data.BookingStatus
import com.durelljardim.kitabu.data.LibraryRepository
import com.durelljardim.kitabu.domain.BookingWithBook
import com.durelljardim.kitabu.domain.toEpochMillis
import com.durelljardim.kitabu.domain.toLocalDate
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val availableOnly = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    // Whenever the query or the filter changes, run the matching database query again.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val books = combine(searchQuery, availableOnly) { query, onlyAvailable ->
        Pair(query, onlyAvailable)
    }.flatMapLatest { (query, onlyAvailable) ->
        if (query.isBlank()) {
            if (onlyAvailable) repository.getAvailableBooks() else repository.getAllBooks()
        } else if (onlyAvailable) {
            // The search query has no availability filter, so drop borrowed books here.
            repository.searchBooks(query.trim()).map { list -> list.filter { it.isAvailable } }
        } else {
            repository.searchBooks(query.trim())
        }
    }

    val uiState: StateFlow<LibraryUiState> = combine(
        books,
        repository.getActiveBookings(),
        searchQuery,
        availableOnly,
        message
    ) { books, bookings, query, onlyAvailable, message ->
        LibraryUiState(
            books = books,
            bookings = bookings,
            searchQuery = query,
            availableOnly = onlyAvailable,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onAvailableOnlyChange(value: Boolean) {
        availableOnly.value = value
    }

    // Called after the snackbar has shown the message so it does not repeat.
    fun messageShown() {
        message.value = null
    }

    fun reserveBook(bookId: Int, userName: String, returnDate: LocalDate?) {
        val name = userName.trim()
        if (name.isEmpty()) {
            message.value = "Enter your name to reserve a book."
            return
        }
        if (returnDate == null) {
            message.value = "Choose a return date."
            return
        }
        if (!returnDate.isAfter(LocalDate.now())) {
            message.value = "The return date must be tomorrow or later."
            return
        }
        viewModelScope.launch {
            repository.reserveBook(bookId, name, returnDate.toEpochMillis())
            message.value = "Book reserved"
        }
    }

    fun markCollected(booking: BookingWithBook) {
        if (booking.status != BookingStatus.PENDING) return
        viewModelScope.launch {
            repository.markCollected(booking.bookingId)
            message.value = "Marked as collected"
        }
    }

    fun cancelBooking(booking: BookingWithBook) {
        if (booking.status != BookingStatus.PENDING) return
        viewModelScope.launch {
            repository.cancelBooking(booking.bookingId, booking.bookOwnerId)
            message.value = "Reservation cancelled"
        }
    }

    fun returnBook(booking: BookingWithBook) {
        if (booking.status != BookingStatus.ACTIVE) return
        viewModelScope.launch {
            repository.returnBook(booking.bookingId, booking.bookOwnerId)
            message.value = "Book returned"
        }
    }

    fun renewBooking(booking: BookingWithBook, newDate: LocalDate?) {
        if (booking.status != BookingStatus.ACTIVE) return
        if (
            newDate == null ||
            !newDate.isAfter(booking.returnDeadline.toLocalDate()) ||
            !newDate.isAfter(LocalDate.now())
        ) {
            message.value = "The new return date must be after the current one and after today."
            return
        }
        viewModelScope.launch {
            repository.renewBooking(booking.bookingId, newDate.toEpochMillis())
            message.value = "Return date updated"
        }
    }
}
