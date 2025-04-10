package com.readtrac.readtrac.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readtrac.readtrac.model.BookEntity
import com.readtrac.readtrac.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling the logic of adding a new book.
 */
@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    /**
     * Adds a new book to the repository.
     *
     * @param title The title of the book.
     * @param author The author of the book.
     * @param genre The genre of the book.
     */
    fun addBook(title: String, author: String, genre: String) {
        viewModelScope.launch {
            val newBook = BookEntity(
                id = 0, // Auto-generated ID
                title = title,
                author = author,
                genre = genre,
                progress = 0f,
                dateAdded = System.currentTimeMillis()
            )
            bookRepository.insertBook(newBook)
        }
    }
}