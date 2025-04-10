package com.readtrac.readtrac.di

import com.readtrac.readtrac.model.dao.BookDao
import com.readtrac.readtrac.model.dao.ReviewDao
import com.readtrac.readtrac.network.BookApiService
import com.readtrac.readtrac.network.NetworkClient
import com.readtrac.readtrac.model.RecommendationEngine
import com.readtrac.readtrac.repository.BookRepository
import com.readtrac.readtrac.repository.IBookRepository
import com.readtrac.readtrac.repository.IReviewRepository
import com.readtrac.readtrac.repository.ReviewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides repository implementations
 * 
 * This module provides repository instances that abstract data operations from
 * the Room database and potentially other external sources.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    /**
     * Provides the book repository implementation
     * 
     * @param bookDao The data access object for books
     * @param recommendationEngine Engine for generating book recommendations
     * @param bookApiService Service for accessing external book API
     * @param networkClient Client for handling network requests
     * @return An implementation of IBookRepository
     */
    @Provides
    @Singleton
    fun provideBookRepository(
        bookDao: BookDao, 
        recommendationEngine: RecommendationEngine,
        bookApiService: BookApiService,
        networkClient: NetworkClient
    ): IBookRepository {
        return BookRepository(bookDao, recommendationEngine, bookApiService, networkClient)
    }
    
    /**
     * Provides the review repository implementation
     * 
     * @param reviewDao The data access object for reviews
     * @return An implementation of IReviewRepository
     */
    @Provides
    @Singleton
    fun provideReviewRepository(reviewDao: ReviewDao): IReviewRepository {
        return ReviewRepository(reviewDao)
    }
}