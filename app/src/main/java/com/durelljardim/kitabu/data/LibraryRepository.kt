package com.durelljardim.kitabu.data

import com.durelljardim.kitabu.domain.BookingWithBook
import kotlinx.coroutines.flow.Flow

class LibraryRepository(
    private val bookDao: BookDao,
    private val bookingDao: BookingDao
) {

    fun getAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooks()

    fun getAvailableBooks(): Flow<List<BookEntity>> = bookDao.getAvailableBooks()

    fun searchBooks(query: String): Flow<List<BookEntity>> = bookDao.searchBooks(query)

    fun getActiveBookings(): Flow<List<BookingWithBook>> = bookingDao.getActiveBookings()

    suspend fun reserveBook(bookId: Int, userName: String, returnDeadline: Long) {
        val booking = BookingEntity(
            bookOwnerId = bookId,
            userName = userName,
            bookingDate = System.currentTimeMillis(),
            returnDeadline = returnDeadline,
            status = BookingStatus.PENDING
        )
        bookingDao.reserveBook(booking)
    }

    suspend fun markCollected(bookingId: Int) {
        bookingDao.updateStatus(bookingId, BookingStatus.ACTIVE)
    }

    suspend fun renewBooking(bookingId: Int, newDeadline: Long) {
        bookingDao.updateReturnDeadline(bookingId, newDeadline)
    }

    suspend fun cancelBooking(bookingId: Int, bookId: Int) {
        bookingDao.cancelBooking(bookingId, bookId)
    }

    suspend fun returnBook(bookingId: Int, bookId: Int) {
        bookingDao.returnBook(bookingId, bookId)
    }
}
