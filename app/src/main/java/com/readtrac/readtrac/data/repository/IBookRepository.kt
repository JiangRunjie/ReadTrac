package com.readtrac.readtrac.data.repository

import com.readtrac.readtrac.data.model.BookEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Book Repository
 *
 * This interface defines the contract for any class that will serve as a repository
 * for book data, abstracting the data source from the rest of the application.
 */
interface IBookRepository {
    /**
     * Get all books as a flow of data
     * 
     * @return A flow emitting all books
     */
    fun getAllBooks(): Flow<List<BookEntity>>
    
    /**
     * Get a book by its unique ID
     * 
     * @param id The ID of the book
     * @return The book with the given ID, or null if not found
     */
    suspend fun getBookById(id: Long): BookEntity?
    
    /**
     * Insert a new book into the data source
     * 
     * @param book The book to insert
     * @return The ID of the newly inserted book
     */
    suspend fun insertBook(book: BookEntity): Long
    
    /**
     * Update an existing book
     * 
     * @param book The book with updated information
     */
    suspend fun updateBook(book: BookEntity)
    
    /**
     * Delete a book from the data source
     * 
     * @param book The book to delete
     */
    suspend fun deleteBook(book: BookEntity)
    
    /**
     * Search for books by title or author
     * 
     * @param query The search query string
     * @return A flow of books matching the query
     */
    fun searchBooks(query: String): Flow<List<BookEntity>>
    
    /**
     * Get books by genre
     * 
     * @param genre The genre to filter by
     * @return A flow of books in the specified genre
     */
    fun getBooksByGenre(genre: String): Flow<List<BookEntity>>
    
    /**
     * Update the reading progress for a book
     * 
     * @param bookId The ID of the book to update
     * @param progress The new progress value (0.0 to 1.0)
     * @return true if the update was successful, false otherwise
     */
    suspend fun updateReadingProgress(bookId: Long, progress: Float): Boolean
    
    /**
     * Get recommended books based on the user's reading history or a specific category
     *
     * @param limit Maximum number of recommendations to return
     * @param category Optional specific category to get recommendations for
     * @return A flow of recommended books
     */
    fun getRecommendedBooks(limit: Int = 5, category: String? = null): Flow<List<BookEntity>>

    /**
     * Search for books in external API
     * 
     * @param query The search query string
     * @param limit Maximum number of results to return
     * @return A flow of books matching the query from external API
     */
    fun searchExternalBooks(query: String, limit: Int = 10): Flow<List<BookEntity>>
}