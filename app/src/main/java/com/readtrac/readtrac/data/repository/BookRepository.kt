package com.readtrac.readtrac.data.repository

import com.readtrac.readtrac.data.dao.BookDao
import com.readtrac.readtrac.data.model.BookEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for accessing book data
 * 
 * This repository abstracts the data sources and provides a clean API
 * for the ViewModel to interact with book data
 * 
 * @property bookDao The data access object for book operations
 */
class BookRepository(private val bookDao: BookDao) {
    
    /**
     * Get all books as a flow of data
     * 
     * @return A flow emitting all books
     */
    fun getAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooks()
    
    /**
     * Get a book by its unique ID
     * 
     * @param id The ID of the book
     * @return The book with the given ID, or null if not found
     */
    suspend fun getBookById(id: Long): BookEntity? = bookDao.getBookById(id)
    
    /**
     * Insert a new book into the database
     * 
     * @param book The book to insert
     * @return The ID of the newly inserted book
     */
    suspend fun insertBook(book: BookEntity): Long = bookDao.insertBook(book)
    
    /**
     * Update an existing book
     * 
     * @param book The book with updated information
     */
    suspend fun updateBook(book: BookEntity) = bookDao.updateBook(book)
    
    /**
     * Delete a book from the database
     * 
     * @param book The book to delete
     */
    suspend fun deleteBook(book: BookEntity) = bookDao.deleteBook(book)
}