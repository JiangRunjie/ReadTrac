package com.readtrac.readtrac.repository

import com.readtrac.readtrac.model.dao.ReviewDao
import com.readtrac.readtrac.model.ReviewEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing review data
 * 
 * This repository abstracts the data source for review data and provides
 * a clean API for higher layers to interact with review data.
 * 
 * @property reviewDao The data access object for review operations
 */
@Singleton
class ReviewRepository @Inject constructor(private val reviewDao: ReviewDao) : IReviewRepository {
    
    /**
     * Get all reviews as a flow of data
     * 
     * @return A flow emitting all reviews
     */
    override fun getAllReviews(): Flow<List<ReviewEntity>> = reviewDao.getAllReviews()
    
    /**
     * Get all reviews for a specific book
     * 
     * @param bookId The ID of the book to get reviews for
     * @return Flow emitting reviews for the specified book
     */
    override fun getReviewsForBook(bookId: Long): Flow<List<ReviewEntity>> = 
        reviewDao.getReviewsForBook(bookId)
    
    /**
     * Get a review by its unique ID
     * 
     * @param id The ID of the review
     * @return The review with the given ID, or null if not found
     */
    override suspend fun getReviewById(id: Long): ReviewEntity? = reviewDao.getReviewById(id)
    
    /**
     * Insert a new review into the database
     * 
     * @param review The review to insert
     * @return The ID of the newly inserted review
     */
    override suspend fun insertReview(review: ReviewEntity): Long = reviewDao.insertReview(review)
    
    /**
     * Update an existing review
     * 
     * @param review The review with updated information
     */
    override suspend fun updateReview(review: ReviewEntity) = reviewDao.updateReview(review)
    
    /**
     * Delete a review from the database
     * 
     * @param review The review to delete
     */
    override suspend fun deleteReview(review: ReviewEntity) = reviewDao.deleteReview(review)
    
    /**
     * Delete all reviews for a specific book
     * 
     * @param bookId The ID of the book to delete reviews for
     * @return The number of reviews deleted
     */
    override suspend fun deleteReviewsByBookId(bookId: Long): Int = 
        reviewDao.deleteReviewsByBookId(bookId)
        
    /**
     * Get public reviews only
     * 
     * @return A flow of reviews marked as public
     */
    override fun getPublicReviews(): Flow<List<ReviewEntity>> = 
        reviewDao.getAllReviews().map { reviews ->
            reviews.filter { it.isPublic }
        }
}