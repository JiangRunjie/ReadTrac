package com.readtrac.readtrac.data.entity

/**
 * UI model class for Review
 *
 * This class represents a review entity to be displayed in the UI.
 * It contains only the properties needed for the UI, simplified from the database entity.
 *
 * @property id Unique identifier for the review
 * @property bookId ID of the book this review is associated with
 * @property reviewText The text content of the review
 * @property timestamp When the review was created/updated (in milliseconds since epoch)
 * @property isPublic Whether the review is marked for public sharing
 */
data class Review(
    val id: Long = 0,
    val bookId: Long,
    val reviewText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPublic: Boolean = false
)