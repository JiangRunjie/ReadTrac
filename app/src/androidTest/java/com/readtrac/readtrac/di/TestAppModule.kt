package com.readtrac.readtrac.di

import android.content.Context
import androidx.room.Room
import com.readtrac.readtrac.database.AppDatabase
import com.readtrac.readtrac.repository.BookRepository
import com.readtrac.readtrac.repository.IBookRepository
import com.readtrac.readtrac.repository.IReviewRepository
import com.readtrac.readtrac.repository.ReviewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class, RepositoryModule::class]
)
object TestAppModule {
    
    @Provides
    @Singleton
    fun provideTestDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Provides
    fun provideBookDao(database: AppDatabase) = database.bookDao()

    @Provides
    fun provideReviewDao(database: AppDatabase) = database.reviewDao()

    @Provides
    @Singleton
    fun provideBookRepository(database: AppDatabase): IBookRepository {
        return BookRepository(database.bookDao())
    }

    @Provides
    @Singleton
    fun provideReviewRepository(database: AppDatabase): IReviewRepository {
        return ReviewRepository(database.reviewDao())
    }
}