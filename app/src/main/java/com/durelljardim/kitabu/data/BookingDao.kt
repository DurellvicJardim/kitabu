package com.durelljardim.kitabu.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.durelljardim.kitabu.domain.BookingWithBook
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Insert
    suspend fun insertBooking(booking: BookingEntity)

    // Lives here because it must run inside the same transactions as the booking changes.
    @Query("UPDATE books SET isAvailable = :isAvailable WHERE bookId = :bookId")
    suspend fun updateBookAvailability(bookId: Int, isAvailable: Boolean)

    @Query("UPDATE bookings SET status = :status WHERE bookingId = :bookingId")
    suspend fun updateStatus(bookingId: Int, status: BookingStatus)

    @Query("UPDATE bookings SET returnDeadline = :returnDeadline WHERE bookingId = :bookingId")
    suspend fun updateReturnDeadline(bookingId: Int, returnDeadline: Long)

    @Query("DELETE FROM bookings WHERE bookingId = :bookingId")
    suspend fun deleteBooking(bookingId: Int)

    @Query(
        """
        SELECT bookings.bookingId, bookings.bookOwnerId, bookings.userName,
               bookings.bookingDate, bookings.returnDeadline, bookings.status,
               books.title, books.author, books.category
        FROM bookings
        JOIN books ON bookings.bookOwnerId = books.bookId
        WHERE bookings.status IN ('PENDING', 'ACTIVE')
        ORDER BY bookings.returnDeadline ASC
        """
    )
    fun getActiveBookings(): Flow<List<BookingWithBook>>

    // Reserve, cancel and return each touch both tables, so they run as one transaction.
    @Transaction
    suspend fun reserveBook(booking: BookingEntity) {
        insertBooking(booking)
        updateBookAvailability(booking.bookOwnerId, false)
    }

    @Transaction
    suspend fun cancelBooking(bookingId: Int, bookId: Int) {
        deleteBooking(bookingId)
        updateBookAvailability(bookId, true)
    }

    @Transaction
    suspend fun returnBook(bookingId: Int, bookId: Int) {
        updateStatus(bookingId, BookingStatus.RETURNED)
        updateBookAvailability(bookId, true)
    }
}
