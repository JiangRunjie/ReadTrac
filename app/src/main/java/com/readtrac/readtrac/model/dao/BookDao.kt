package com.readtrac.readtrac.model.dao

import androidx.room.*
import com.readtrac.readtrac.model.BookEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Book entities
 * 
 * Provides methods to query, insert, update, and delete books in the database
 */
@Dao
interface BookDao {
    /**
     * Get all books as a Flow
     * 
     * @return Flow emitting all books in the database
     */
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>
    
    /**
     * Get a specific book by ID
     * 
     * @param id The book ID to retrieve
     * @return The book with the specified ID, or null if not found
     */
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Long): BookEntity?
    
    /**
     * Insert a new book into the database
     * 
     * @param book The book to insert
     * @return The ID of the newly inserted book
     */
    @Insert
    suspend fun insertBook(book: BookEntity): Long
    
    /**
     * Update an existing book
     * 
     * @param book The book to update
     */
    @Update
    suspend fun updateBook(book: BookEntity)
    
    /**
     * Delete a book from the database
     * 
     * @param book The book to delete
     */
    @Delete
    suspend fun deleteBook(book: BookEntity)
}