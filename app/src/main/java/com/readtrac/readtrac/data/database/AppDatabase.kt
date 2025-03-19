package com.readtrac.readtrac.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.readtrac.readtrac.data.dao.BookDao
import com.readtrac.readtrac.data.model.BookEntity

/**
 * Main database class for the ReadTrac application
 * 
 * This class serves as the main access point for the underlying
 * SQLite database. It provides the DAOs that can be used to
 * interact with the database.
 */
@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Returns the BookDao to access book data
     */
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets or creates the singleton database instance
         * 
         * @param context Application context
         * @return The database instance
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "readtrac_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}