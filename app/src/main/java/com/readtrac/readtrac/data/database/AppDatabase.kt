package com.readtrac.readtrac.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.readtrac.readtrac.data.dao.BookDao
import com.readtrac.readtrac.data.dao.ReviewDao
import com.readtrac.readtrac.data.model.BookEntity
import com.readtrac.readtrac.data.model.ReviewEntity

/**
 * Main database class for the ReadTrac application
 * 
 * This class serves as the main access point for the underlying
 * SQLite database. It provides the DAOs that can be used to
 * interact with the database.
 */
@Database(
    entities = [BookEntity::class, ReviewEntity::class], 
    version = 2, // Updated from 1 to 2 because of schema changes to BookEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Returns the BookDao to access book data
     */
    abstract fun bookDao(): BookDao
    
    /**
     * Returns the ReviewDao to access review data
     */
    abstract fun reviewDao(): ReviewDao
    
    companion object {
        /**
         * Migration from version 1 to version 2 - Adding new columns to BookEntity
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to the books table
                database.execSQL("ALTER TABLE books ADD COLUMN isbn TEXT")
                database.execSQL("ALTER TABLE books ADD COLUMN coverUrl TEXT")
                database.execSQL("ALTER TABLE books ADD COLUMN description TEXT")
                database.execSQL("ALTER TABLE books ADD COLUMN isExternal INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}