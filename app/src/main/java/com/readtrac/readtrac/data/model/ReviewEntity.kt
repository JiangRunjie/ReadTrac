package com.readtrac.readtrac.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a book review in the database
 *
 * This class is used to store detailed reviews that a user writes for books.
 * It maintains a foreign key relationship with the BookEntity class.
 *
 * @property id Unique identifier for the review
 * @property bookId Foreign key reference to the associated book
 * @property reviewText The text content of the review
 * @property timestamp When the review was created/updated (in milliseconds since epoch)
 * @property isPublic Whether the review is marked for public sharing
 */
@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE // Delete reviews when the parent book is deleted
        )
    ],
    indices = [Index("bookId")] // Index for faster queries on foreign key
)
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val reviewText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPublic: Boolean = false
)