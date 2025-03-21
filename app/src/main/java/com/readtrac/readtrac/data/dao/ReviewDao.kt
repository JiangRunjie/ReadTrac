package com.readtrac.readtrac.data.dao

import androidx.room.*
import com.readtrac.readtrac.data.model.ReviewEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Review entities
 * 
 * Provides methods to query, insert, update, and delete reviews in the database
 */
@Dao
interface ReviewDao {
    /**
     * Get all reviews as a Flow
     * 
     * @return Flow emitting all reviews in the database
     */
    @Query("SELECT * FROM reviews")
    fun getAllReviews(): Flow<List<ReviewEntity>>
    
    /**
     * Get all reviews for a specific book
     * 
     * @param bookId The ID of the book to get reviews for
     * @return Flow emitting reviews for the specified book
     */
    @Query("SELECT * FROM reviews WHERE bookId = :bookId")
    fun getReviewsForBook(bookId: Long): Flow<List<ReviewEntity>>
    
    /**
     * Get a specific review by ID
     * 
     * @param id The review ID to retrieve
     * @return The review with the specified ID, or null if not found
     */
    @Query("SELECT * FROM reviews WHERE id = :id")
    suspend fun getReviewById(id: Long): ReviewEntity?
    
    /**
     * Insert a new review into the database
     * 
     * @param review The review to insert
     * @return The ID of the newly inserted review
     */
    @Insert
    suspend fun insertReview(review: ReviewEntity): Long
    
    /**
     * Update an existing review
     * 
     * @param review The review to update
     */
    @Update
    suspend fun updateReview(review: ReviewEntity)
    
    /**
     * Delete a review from the database
     * 
     * @param review The review to delete
     */
    @Delete
    suspend fun deleteReview(review: ReviewEntity)
    
    /**
     * Delete all reviews for a specific book
     * 
     * @param bookId The ID of the book to delete reviews for
     * @return The number of reviews deleted
     */
    @Query("DELETE FROM reviews WHERE bookId = :bookId")
    suspend fun deleteReviewsByBookId(bookId: Long): Int
}