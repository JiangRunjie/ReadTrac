package com.readtrac.readtrac.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readtrac.readtrac.data.model.ReviewEntity
import com.readtrac.readtrac.data.repository.IReviewRepository
import com.readtrac.readtrac.data.entity.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for review-related operations
 * 
 * Acts as an interface between the UI and data layers, providing review data
 * to the UI and handling user interactions that affect review data.
 *
 * @property repository The repository that provides access to review data
 */
@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val repository: IReviewRepository
) : ViewModel() {
    
    /**
     * Get all reviews as a flow of UI model reviews
     * 
     * @return Flow of review lists for the UI
     */
    val reviews: Flow<List<Review>> = repository.getAllReviews().map { entities ->
        entities.map { it.toUiModel() }
    }
    
    /**
     * Get reviews for a specific book
     * 
     * @param bookId The ID of the book to get reviews for
     * @return Flow of review lists for the UI
     */
    fun getReviewsForBook(bookId: Long): Flow<List<Review>> {
        return repository.getReviewsForBook(bookId).map { entities ->
            entities.map { it.toUiModel() }
        }
    }
    
    /**
     * Get only public reviews
     * 
     * @return Flow of public review lists for the UI
     */
    fun getPublicReviews(): Flow<List<Review>> {
        return repository.getPublicReviews().map { entities ->
            entities.map { it.toUiModel() }
        }
    }
    
    /**
     * Add a new review to the database
     * 
     * @param bookId The ID of the book being reviewed
     * @param reviewText The text content of the review
     * @param isPublic Whether the review should be publicly available
     * @return The ID of the newly added review
     */
    suspend fun addReview(bookId: Long, reviewText: String, isPublic: Boolean = false): Long {
        val reviewEntity = ReviewEntity(
            bookId = bookId,
            reviewText = reviewText,
            isPublic = isPublic
        )
        return repository.insertReview(reviewEntity)
    }
    
    /**
     * Update an existing review
     * 
     * @param reviewId The ID of the review to update
     * @param reviewText The new text content
     * @param isPublic The new public visibility status
     */
    fun updateReview(reviewId: Long, reviewText: String, isPublic: Boolean) {
        viewModelScope.launch {
            val review = repository.getReviewById(reviewId)
            review?.let {
                val updatedReview = it.copy(reviewText = reviewText, isPublic = isPublic)
                repository.updateReview(updatedReview)
            }
        }
    }
    
    /**
     * Delete a review
     * 
     * @param reviewId The ID of the review to delete
     */
    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            val review = repository.getReviewById(reviewId)
            review?.let {
                repository.deleteReview(it)
            }
        }
    }
    
    /**
     * Delete all reviews for a specific book
     * 
     * @param bookId The ID of the book to delete reviews for
     * @return Number of reviews deleted
     */
    suspend fun deleteReviewsByBookId(bookId: Long): Int {
        return repository.deleteReviewsByBookId(bookId)
    }
    
    /**
     * Convert a database entity to a UI model
     */
    private fun ReviewEntity.toUiModel(): Review {
        return Review(
            id = this.id,
            bookId = this.bookId,
            reviewText = this.reviewText,
            timestamp = this.timestamp,
            isPublic = this.isPublic
        )
    }
}