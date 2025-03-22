package com.readtrac.readtrac.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 1, 
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
}