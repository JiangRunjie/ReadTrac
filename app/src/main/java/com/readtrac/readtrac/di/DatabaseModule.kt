package com.readtrac.readtrac.di

import android.content.Context
import androidx.room.Room
import com.readtrac.readtrac.model.dao.BookDao
import com.readtrac.readtrac.model.dao.ReviewDao
import com.readtrac.readtrac.database.AppDatabase
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
        )
            // Add the migration from version 1 to version 2
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
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