package com.durelljardim.kitabu.di

import android.content.Context
import com.durelljardim.kitabu.data.KitabuDatabase
import com.durelljardim.kitabu.data.LibraryRepository

class AppContainer(context: Context) {

    val repository: LibraryRepository by lazy {
        val database = KitabuDatabase.getDatabase(context)
        LibraryRepository(database.bookDao(), database.bookingDao())
    }
}
