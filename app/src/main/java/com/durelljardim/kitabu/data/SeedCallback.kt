package com.durelljardim.kitabu.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.durelljardim.kitabu.domain.toEpochMillis
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SeedCallback(private val databaseProvider: () -> KitabuDatabase) : RoomDatabase.Callback() {

    // onCreate runs before the database instance exists, so seeding waits for it off the main thread.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch {
            seed(databaseProvider())
        }
    }

    private suspend fun seed(database: KitabuDatabase) {
        val books = listOf(
            BookEntity(title = "Introduction to Information Systems", author = "Rainer & Prince", category = "Information Systems"),
            BookEntity(title = "Python Crash Course", author = "Eric Matthes", category = "Programming"),
            BookEntity(title = "Head First Java", author = "Sierra & Bates", category = "Programming", isAvailable = false),
            BookEntity(title = "Database System Concepts", author = "Silberschatz, Korth & Sudarshan", category = "Databases", isAvailable = false),
            BookEntity(title = "HTML and CSS: Design and Build Websites", author = "Jon Duckett", category = "Web Design", isAvailable = false),
            BookEntity(title = "JavaScript and jQuery", author = "Jon Duckett", category = "Web Development"),
            BookEntity(title = "Learning PHP, MySQL and JavaScript", author = "Robin Nixon", category = "Web Development"),
            BookEntity(title = "SVG Animations", author = "Sarah Drasner", category = "Web Animation"),
            BookEntity(title = "WordPress: The Missing Manual", author = "Matthew MacDonald", category = "Content Management"),
            BookEntity(title = "Head First Android Development", author = "Griffiths & Griffiths", category = "Mobile Development", isAvailable = false),
            BookEntity(title = "Computer Security: Principles and Practice", author = "Stallings & Brown", category = "Information Security"),
            BookEntity(title = "Statistics for Business and Economics", author = "Anderson, Sweeney & Williams", category = "Statistics"),
            BookEntity(title = "Research Methods for Business Students", author = "Saunders, Lewis & Thornhill", category = "Research")
        )
        // The ids come back in list order, so each booking can point at its own book without guessing.
        val ids = database.bookDao().insertBooks(books)

        val today = LocalDate.now()
        val bookings = listOf(
            BookingEntity(
                bookOwnerId = ids[2].toInt(),
                userName = "Durell Jardim",
                bookingDate = today.minusDays(10).toEpochMillis(),
                returnDeadline = today.minusDays(4).toEpochMillis(),
                status = BookingStatus.ACTIVE
            ),
            BookingEntity(
                bookOwnerId = ids[9].toInt(),
                userName = "Katlego Raletsemo",
                bookingDate = today.minusDays(12).toEpochMillis(),
                returnDeadline = today.toEpochMillis(),
                status = BookingStatus.ACTIVE
            ),
            BookingEntity(
                bookOwnerId = ids[3].toInt(),
                userName = "Ethan Peters",
                bookingDate = today.minusDays(5).toEpochMillis(),
                returnDeadline = today.plusDays(9).toEpochMillis(),
                status = BookingStatus.ACTIVE
            ),
            BookingEntity(
                bookOwnerId = ids[4].toInt(),
                userName = "Megan Johnston",
                bookingDate = today.toEpochMillis(),
                returnDeadline = today.plusDays(7).toEpochMillis(),
                status = BookingStatus.PENDING
            )
        )
        bookings.forEach { database.bookingDao().insertBooking(it) }
    }
}

