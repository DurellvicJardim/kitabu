package com.durelljardim.kitabu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [BookEntity::class, BookingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KitabuDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    abstract fun bookingDao(): BookingDao

    companion object {

        @Volatile
        private var Instance: KitabuDatabase? = null

        fun getDatabase(context: Context): KitabuDatabase {
            return Instance ?: synchronized(this) {
                Instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    KitabuDatabase::class.java,
                    "kitabu_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(SeedCallback { getDatabase(context) })
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
