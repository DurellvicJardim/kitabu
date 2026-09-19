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

    companion object {

        @Volatile
        private var Instance: KitabuDatabase? = null

        fun getDatabase(context: Context): KitabuDatabase {
            // Only one thread may create the database, and only once.
            return Instance ?: synchronized(this) {
                Instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    KitabuDatabase::class.java,
                    "kitabu_database"
                )
                    // While the schema is still changing, wipe and rebuild instead of migrating.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
