package com.readtrac.readtrac.data.repository

import com.readtrac.readtrac.data.dao.BookDao
import com.readtrac.readtrac.data.model.BookEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing book data
 * 
 * This repository abstracts the data sources and provides a clean API
 * for the ViewModel to interact with book data
 * 
 * @property bookDao The data access object for book operations
 */
@Singleton
class BookRepository @Inject constructor(private val bookDao: BookDao) : IBookRepository {
    
    /**
     * Get all books as a flow of data
     * 
     * @return A flow emitting all books
     */
    override fun getAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooks()
    
    /**
     * Get a book by its unique ID
     * 
     * @param id The ID of the book
     * @return The book with the given ID, or null if not found
     */
    override suspend fun getBookById(id: Long): BookEntity? = bookDao.getBookById(id)
    
    /**
     * Insert a new book into the database
     * 
     * @param book The book to insert
     * @return The ID of the newly inserted book
     */
    override suspend fun insertBook(book: BookEntity): Long = bookDao.insertBook(book)
    
    /**
     * Update an existing book
     * 
     * @param book The book with updated information
     */
    override suspend fun updateBook(book: BookEntity) = bookDao.updateBook(book)
    
    /**
     * Delete a book from the database
     * 
     * @param book The book to delete
     */
    override suspend fun deleteBook(book: BookEntity) = bookDao.deleteBook(book)
    
    /**
     * Search for books by title or author
     *
     * @param query The search query string
     * @return A flow of books matching the query
     */
    override fun searchBooks(query: String): Flow<List<BookEntity>> {
        return bookDao.getAllBooks().map { books ->
            books.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.author.contains(query, ignoreCase = true)
            }
        }
    }
    
    /**
     * Get books by genre
     *
     * @param genre The genre to filter by
     * @return A flow of books in the specified genre
     */
    override fun getBooksByGenre(genre: String): Flow<List<BookEntity>> {
        return bookDao.getAllBooks().map { books ->
            books.filter {
                it.genre?.equals(genre, ignoreCase = true) ?: false
            }
        }
    }
    
    /**
     * Update the reading progress for a book
     *
     * @param bookId The ID of the book to update
     * @param progress The new progress value (0.0 to 1.0)
     * @return true if the update was successful, false otherwise
     */
    override suspend fun updateReadingProgress(bookId: Long, progress: Float): Boolean {
        val book = bookDao.getBookById(bookId) ?: return false
        
        // Ensure progress is within valid range (0-1)
        val validatedProgress = progress.coerceIn(0f, 1f)
        
        // Create a copy with updated progress
        val updatedBook = book.copy(progress = validatedProgress)
        
        // Update the book
        bookDao.updateBook(updatedBook)
        return true
    }
}