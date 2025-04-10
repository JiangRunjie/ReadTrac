package com.readtrac.readtrac.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a book in the database
 *
 * @property id Unique identifier for the book
 * @property title The title of the book
 * @property author The author of the book
 * @property progress The reading progress as a float between 0 and 1
 * @property rating The user's rating of the book (0-5 stars, can be null if not rated)
 * @property genre The book's genre classification (optional)
 * @property publishedDate The book's publication date (optional)
 * @property pageCount The total number of pages in the book (optional)
 * @property dateAdded Date when the book was added to the reading list
 * @property summary Optional summary of the book
 * @property notes Optional notes about the book
 * @property isbn ISBN identifier for the book (optional)
 * @property coverUrl URL to the book cover image (optional)
 * @property description More detailed description of the book (optional)
 * @property isExternal Flag to indicate if the book came from an external API
 */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val progress: Float = 0f,
    val rating: Float? = null,
    val genre: String? = null,
    val publishedDate: String? = null,
    val pageCount: Int? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val summary: String? = null,
    val notes: String? = null,
    val isbn: String? = null,
    val coverUrl: String? = null,
    val description: String? = null,
    val isExternal: Boolean = false
)