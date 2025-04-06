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
    
    /**
     * Load book recommendations
     *
     * @param limit Maximum number of recommendations to fetch
     * @param useNetworkOnly If true, only fetch from network and ignore local data
     */
    fun loadRecommendations(limit: Int = 5, useNetworkOnly: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            bookRepository.getRecommendedBooks(limit)
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
     * Refresh book recommendations
     */
    fun refreshRecommendations() {
        loadRecommendations(useNetworkOnly = true)
    }
    
    /**
     * Map a BookEntity to a Book
     */
    private fun mapToBook(bookEntity: BookEntity): Book {
        return Book(
            id = bookEntity.id,
            title = bookEntity.title,
            author = bookEntity.author,
            rating = bookEntity.rating,
            genre = bookEntity.genre,
            coverUrl = bookEntity.coverUrl,
            isExternal = bookEntity.isExternal
        )
    }
}