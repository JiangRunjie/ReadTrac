package com.readtrac.readtrac.di

import android.content.Context
import androidx.room.Room
import com.readtrac.readtrac.data.dao.BookDao
import com.readtrac.readtrac.data.dao.ReviewDao
import com.readtrac.readtrac.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides database-related dependencies
 * 
 * This module is responsible for providing the application database
 * instance and data access objects (DAOs) for dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the Room database instance
     * 
     * @param context The application context
     * @return A singleton instance of AppDatabase
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "readtrac_database"
        ).build()
    }

    /**
     * Provides the BookDao
     * 
     * @param database The application database
     * @return An instance of BookDao
     */
    @Provides
    fun provideBookDao(database: AppDatabase): BookDao {
        return database.bookDao()
    }

    /**
     * Provides the ReviewDao
     * 
     * @param database The application database
     * @return An instance of ReviewDao
     */
    @Provides
    fun provideReviewDao(database: AppDatabase): ReviewDao {
        return database.reviewDao()
    }
}