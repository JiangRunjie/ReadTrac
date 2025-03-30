package com.readtrac.readtrac.data.entity

/**
 * UI model class for Book
 *
 * This class represents a book entity to be displayed in the UI.
 * It contains only the properties needed for the UI, simplified from the database entity.
 *
 * @property id Unique identifier for the book
 * @property title Title of the book
 * @property author Author of the book
 * @property progress Reading progress (0.0-1.0)
 * @property rating Optional user rating (0-5 stars)
 * @property genre Optional genre of the book
 * @property notes Optional user notes about the book
 */
data class Book(
    val id: Long = 0,
    val title: String,
    val author: String,
    val progress: Float = 0f,
    val rating: Float? = null,
    val genre: String? = null,
    val notes: String? = null
)