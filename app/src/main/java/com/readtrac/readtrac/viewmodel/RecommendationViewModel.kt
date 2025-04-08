package com.readtrac.readtrac.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readtrac.readtrac.data.entity.Book
import com.readtrac.readtrac.data.model.BookEntity
import com.readtrac.readtrac.data.recommendation.RecommendationEngine
import com.readtrac.readtrac.data.repository.IBookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * ViewModel for the recommendation screen
 *
 * This ViewModel is responsible for fetching and managing book recommendations
 * based on the user's reading history and preferences.
 */
@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val bookRepository: IBookRepository,
    private val recommendationEngine: RecommendationEngine
) : ViewModel() {
    
    // State for recommended books
    private val _recommendedBooks = MutableStateFlow<List<Book>>(emptyList())
    val recommendedBooks: StateFlow<List<Book>> = _recommendedBooks
    
    // State for loading indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // State for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // State to track if we're using external recommendations
    private val _isExternalSource = MutableStateFlow(false)
    val isExternalSource: StateFlow<Boolean> = _isExternalSource
    
    // Store a set of previously used categories to avoid repetition
    private val usedCategories = mutableSetOf<String>()
    
    // List of genres to use when refreshing for new batches
    private val genreOptions = listOf(
        "fiction", "fantasy", "mystery", "sci-fi", "romance", 
        "thriller", "biography", "history", "science", "philosophy",
        "self-help", "business", "politics", "travel", "art"
    )
    
    // Initialize by loading recommendations
    init {
        loadRecommendations()
    }
    
    /**
     * Load book recommendations
     *
     * @param limit Maximum number of recommendations to fetch
     * @param useNetworkOnly If true, only fetch from network and ignore local data
     * @param category Optional specific category to fetch books for
     */
    fun loadRecommendations(
        limit: Int = 5, 
        useNetworkOnly: Boolean = false,
        category: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            bookRepository.getRecommendedBooks(limit, category)
                .catch { e ->
                    _errorMessage.value = "Failed to load recommendations: ${e.message}"
                    _isLoading.value = false
                }
                .collect { bookEntities ->
                    // Map the BookEntity objects to Book objects
                    val books = bookEntities.map { mapToBook(it) }
                    
                    // Update states
                    _recommendedBooks.value = books
                    _isExternalSource.value = bookEntities.any { it.isExternal }
                    _isLoading.value = false
                }
        }
    }
    
    /**
     * Refresh book recommendations, optionally forcing a new batch of different books
     * 
     * @param forceNewBatch If true, ensures a different genre/category is used to fetch new books
     */
    fun refreshRecommendations(forceNewBatch: Boolean = false) {
        if (forceNewBatch) {
            // Select a random genre that hasn't been used recently
            val availableGenres = genreOptions.filter { !usedCategories.contains(it) }
            val nextGenre = if (availableGenres.isNotEmpty()) {
                availableGenres.random()
            } else {
                // If all genres have been used, clear and start over
                usedCategories.clear()
                genreOptions.random()
            }
            
            // Remember this genre
            usedCategories.add(nextGenre)
            
            // Load recommendations with the new genre
            loadRecommendations(useNetworkOnly = true, category = nextGenre)
        } else {
            // Regular refresh without changing genre
            loadRecommendations(useNetworkOnly = true)
        }
    }
    
    /**
     * Map a BookEntity to a Book
     */
    private fun mapToBook(bookEntity: BookEntity): Book {
        return Book(
            id = bookEntity.id,
            title = bookEntity.title,
            author = bookEntity.author,
            progress = bookEntity.progress,
            rating = bookEntity.rating,
            genre = bookEntity.genre,
            notes = bookEntity.notes,
            coverUrl = bookEntity.coverUrl,
            isExternal = bookEntity.isExternal
        )
    }
}