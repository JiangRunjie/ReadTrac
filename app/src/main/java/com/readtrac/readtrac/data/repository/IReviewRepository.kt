package com.readtrac.readtrac.data.repository

import com.readtrac.readtrac.data.model.ReviewEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Review Repository
 *
 * This interface defines the contract for any class that will serve as a repository
 * for review data, abstracting the data source from the rest of the application.
 */
interface IReviewRepository {
    /**
     * Get all reviews as a flow of data
     * 
     * @return A flow emitting all reviews
     */
    fun getAllReviews(): Flow<List<ReviewEntity>>
    
    /**
     * Get all reviews for a specific book
     * 
     * @param bookId The ID of the book to get reviews for
     * @return Flow emitting reviews for the specified book
     */
    fun getReviewsForBook(bookId: Long): Flow<List<ReviewEntity>>
    
    /**
     * Get a review by its unique ID
     * 
     * @param id The ID of the review
     * @return The review with the given ID, or null if not found
     */
    suspend fun getReviewById(id: Long): ReviewEntity?
    
    /**
     * Insert a new review into the data source
     * 
     * @param review The review to insert
     * @return The ID of the newly inserted review
     */
    suspend fun insertReview(review: ReviewEntity): Long
    
    /**
     * Update an existing review
     * 
     * @param review The review with updated information
     */
    suspend fun updateReview(review: ReviewEntity)
    
    /**
     * Delete a review from the data source
     * 
     * @param review The review to delete
     */
    suspend fun deleteReview(review: ReviewEntity)
    
    /**
     * Delete all reviews for a specific book
     * 
     * @param bookId The ID of the book to delete reviews for
     * @return The number of reviews deleted
     */
    suspend fun deleteReviewsByBookId(bookId: Long): Int
    
    /**
     * Get public reviews only
     * 
     * @return A flow of reviews marked as public
     */
    fun getPublicReviews(): Flow<List<ReviewEntity>>
}