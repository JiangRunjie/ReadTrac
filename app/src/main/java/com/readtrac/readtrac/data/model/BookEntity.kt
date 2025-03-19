package com.readtrac.readtrac.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a book in the database
 *
 * @property id Unique identifier for the book
 * @property title The title of the book
 * @property author The author of the book
 * @property progress The reading progress as a float between 0 and 1
 * @property summary Optional summary of the book
 * @property notes Optional notes about the book
 */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val progress: Float = 0f,
    val summary: String? = null,
    val notes: String? = null
)