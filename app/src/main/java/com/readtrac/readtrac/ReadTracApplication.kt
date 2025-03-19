package com.readtrac.readtrac

import android.app.Application
import com.readtrac.readtrac.data.database.AppDatabase
import com.readtrac.readtrac.data.repository.BookRepository

/**
 * Custom Application class for ReadTrac
 * 
 * Initializes application-wide dependencies and provides access to them.
 * This class is responsible for creating and maintaining the application's
 * database and repository instances.
 */
class ReadTracApplication : Application() {
    // Lazy initialize the database so it's only created when needed
    private val database by lazy { AppDatabase.getInstance(this) }
    
    // Repository that will be used throughout the app
    val bookRepository by lazy { BookRepository(database.bookDao()) }

    override fun onCreate() {
        super.onCreate()
        // Additional initialization can be placed here
    }
}