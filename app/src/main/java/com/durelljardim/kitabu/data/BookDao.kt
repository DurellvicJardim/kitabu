package com.durelljardim.kitabu.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert
    suspend fun insertBooks(books: List<BookEntity>): List<Long>

    @Query("SELECT * FROM books ORDER BY title")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isAvailable = 1 ORDER BY title")
    fun getAvailableBooks(): Flow<List<BookEntity>>

    // The || joins the query to the % so the search matches anywhere in the text.
    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' ORDER BY title")
    fun searchBooks(query: String): Flow<List<BookEntity>>
}
