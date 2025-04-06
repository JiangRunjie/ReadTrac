package com.readtrac.readtrac.data.repository

import com.readtrac.readtrac.data.dao.BookDao
import com.readtrac.readtrac.data.model.BookEntity
import com.readtrac.readtrac.data.network.BookApiMapper
import com.readtrac.readtrac.data.network.BookApiService
import com.readtrac.readtrac.data.network.NetworkClient
import com.readtrac.readtrac.data.network.NetworkResult
import com.readtrac.readtrac.data.recommendation.RecommendationEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing book data
 * 
 * This repository abstracts the data sources and provides a clean API
 * for the ViewModel to interact with book data
 * 
 * @property bookDao The data access object for book operations
 * @property recommendationEngine Engine for generating book recommendations
 * @property bookApiService Service for accessing external book API
 * @property networkClient Client for handling network requests
 */
@Singleton
class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val recommendationEngine: RecommendationEngine,
    private val bookApiService: BookApiService,
    private val networkClient: NetworkClient
) : IBookRepository {
    
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
    
    /**
     * Get recommended books based on the user's reading history
     * Enhanced to fetch recommendations from external API when available
     *
     * @param limit Maximum number of recommendations to return
     * @return A flow of recommended books
     */
    override fun getRecommendedBooks(limit: Int): Flow<List<BookEntity>> = flow {
        // First, try to get user preferences from local database
        val userBooks = bookDao.getAllBooks().first()
        
        // If we have books in the local database, use them to determine genres for API recommendations
        if (userBooks.isNotEmpty()) {
            // Extract most common genres from user's books
            val genres = userBooks
                .mapNotNull { it.genre }
                .groupBy { it }
                .maxByOrNull { it.value.size }
                ?.key
                
            // If we have a genre, try to get recommendations from API
            if (!genres.isNullOrEmpty()) {
                try {
                    // Use external API to fetch recommendations based on genre
                    val externalRecommendationsFlow = networkClient.executeApiCall {
                        bookApiService.getRecommendations("subject:$genres", limit)
                    }
                    
                    // Process the API response
                    externalRecommendationsFlow.collect { result ->
                        when (result) {
                            is NetworkResult.Success -> {
                                // Map API response to BookEntity objects
                                val externalBooks = BookApiMapper.mapToBookEntities(result.data.items)
                                emit(externalBooks)
                            }
                            is NetworkResult.Error -> {
                                // On error, fall back to local recommendations
                                val localRecommendations = recommendationEngine.getRecommendations(
                                    userBooks = userBooks,
                                    availableBooks = userBooks,
                                    limit = limit
                                )
                                emit(localRecommendations)
                            }
                            is NetworkResult.Loading -> {
                                // Do nothing while loading - we'll emit when we have data
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Fall back to local recommendations on any error
                    val localRecommendations = recommendationEngine.getRecommendations(
                        userBooks = userBooks,
                        availableBooks = userBooks,
                        limit = limit
                    )
                    emit(localRecommendations)
                }
            } else {
                // No genres, use local recommendations
                val localRecommendations = recommendationEngine.getRecommendations(
                    userBooks = userBooks,
                    availableBooks = userBooks,
                    limit = limit
                )
                emit(localRecommendations)
            }
        } else {
            // For new users with no books, get popular recommendations
            try {
                val defaultRecommendationsFlow = networkClient.executeApiCall {
                    bookApiService.getRecommendations("subject:fiction", limit)
                }
                
                defaultRecommendationsFlow.collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            // Map API response to BookEntity objects
                            val externalBooks = BookApiMapper.mapToBookEntities(result.data.items)
                            emit(externalBooks)
                        }
                        is NetworkResult.Error, is NetworkResult.Loading -> {
                            // For new users, if API fails, just emit empty list
                            emit(emptyList())
                        }
                    }
                }
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }
    
    /**
     * Search for books in the external API
     *
     * @param query The search query string
     * @param limit Maximum number of results to return
     * @return A flow of BookEntity objects matching the search query
     */
    override fun searchExternalBooks(query: String, limit: Int): Flow<List<BookEntity>> {
        return networkClient.executeApiCall {
            bookApiService.searchBooks(query, limit)
        }.map { result ->
            when (result) {
                is NetworkResult.Success -> BookApiMapper.mapToBookEntities(result.data.items)
                else -> emptyList()
            }
        }.catch {
            emit(emptyList())
        }
    }
}