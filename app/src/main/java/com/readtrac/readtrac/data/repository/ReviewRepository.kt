package com.readtrac.readtrac.data.repository

import com.readtrac.readtrac.data.dao.ReviewDao
import com.readtrac.readtrac.data.model.ReviewEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for accessing review data
 * 
 * This repository abstracts the data source for review data and provides
 * a clean API for higher layers to interact with review data.
 * 
 * @property reviewDao The data access object for review operations
 */
class ReviewRepository(private val reviewDao: ReviewDao) {
    
    /**
     * Get all reviews as a flow of data
     * 
     * @return A flow emitting all reviews
     */
    fun getAllReviews(): Flow<List<ReviewEntity>> = reviewDao.getAllReviews()
    
    /**
     * Get all reviews for a specific book
     * 
     * @param bookId The ID of the book to get reviews for
     * @return Flow emitting reviews for the specified book
     */
    fun getReviewsForBook(bookId: Long): Flow<List<ReviewEntity>> = 
        reviewDao.getReviewsForBook(bookId)
    
    /**
     * Get a review by its unique ID
     * 
     * @param id The ID of the review
     * @return The review with the given ID, or null if not found
     */
    suspend fun getReviewById(id: Long): ReviewEntity? = reviewDao.getReviewById(id)
    
    /**
     * Insert a new review into the database
     * 
     * @param review The review to insert
     * @return The ID of the newly inserted review
     */
    suspend fun insertReview(review: ReviewEntity): Long = reviewDao.insertReview(review)
    
    /**
     * Update an existing review
     * 
     * @param review The review with updated information
     */
    suspend fun updateReview(review: ReviewEntity) = reviewDao.updateReview(review)
    
    /**
     * Delete a review from the database
     * 
     * @param review The review to delete
     */
    suspend fun deleteReview(review: ReviewEntity) = reviewDao.deleteReview(review)
    
    /**
     * Delete all reviews for a specific book
     * 
     * @param bookId The ID of the book to delete reviews for
     * @return The number of reviews deleted
     */
    suspend fun deleteReviewsByBookId(bookId: Long): Int = 
        reviewDao.deleteReviewsByBookId(bookId)
}