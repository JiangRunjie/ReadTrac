package com.readtrac.readtrac.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readtrac.readtrac.model.BookEntity
import com.readtrac.readtrac.repository.IBookRepository
import com.readtrac.readtrac.model.entity.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for book-related operations
 * 
 * Acts as an interface between the UI and data layers, providing book data
 * to the UI and handling user interactions that affect book data.
 *
 * @property repository The repository that provides access to book data
 */
@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: IBookRepository
) : ViewModel() {
    
    /**
     * Get all books as a flow of UI model books
     * 
     * @return Flow of book lists for the UI
     */
    val books: Flow<List<Book>> = repository.getAllBooks().map { entities ->
        entities.map { it.toUiModel() }
    }
    
    /**
     * Add a new book to the database
     * 
     * @param title The title of the book
     * @param author The author of the book
     * @return The ID of the newly added book
     */
    suspend fun addBook(title: String, author: String): Long {
        val bookEntity = BookEntity(
            title = title,
            author = author,
            progress = 0f
        )
        return repository.insertBook(bookEntity)
    }
    
    /**
     * Update the reading progress for a book
     * 
     * @param id The ID of the book to update
     * @param progress The new reading progress (0.0-1.0)
     */
    fun updateProgress(id: Long, progress: Float) {
        viewModelScope.launch {
            repository.updateReadingProgress(id, progress)
        }
    }
    
    /**
     * Update book notes
     *
     * @param id The ID of the book to update
     * @param notes The new notes for the book
     */
    fun updateNotes(id: Long, notes: String) {
        viewModelScope.launch {
            val book = repository.getBookById(id)
            book?.let {
                repository.updateBook(it.copy(notes = notes))
            }
        }
    }
    
    /**
     * Delete a book from the database
     * 
     * @param id The ID of the book to delete
     */
    fun deleteBook(id: Long) {
        viewModelScope.launch {
            val book = repository.getBookById(id)
            book?.let {
                repository.deleteBook(it)
            }
        }
    }
    
    /**
     * Convert a database entity to a UI model
     */
    private fun BookEntity.toUiModel(): Book {
        return Book(
            id = this.id,
            title = this.title,
            author = this.author,
            progress = this.progress,
            rating = this.rating,
            genre = this.genre,
            notes = this.notes
        )
    }
}