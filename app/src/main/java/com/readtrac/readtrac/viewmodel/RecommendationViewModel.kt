package com.readtrac.readtrac.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readtrac.readtrac.data.entity.Book
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
    
    /**
     * Load book recommendations
     *
     * @param limit Maximum number of recommendations to fetch
     */
    fun loadRecommendations(limit: Int = 5) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            bookRepository.getRecommendedBooks(limit)
                .catch { e ->
                    _errorMessage.value = "Failed to load recommendations: ${e.message}"
                    _isLoading.value = false
                }
                .collect { bookEntities ->
                    _recommendedBooks.value = recommendationEngine.mapToBookEntities(bookEntities)
                    _isLoading.value = false
                }
        }
    }
    
    /**
     * Refresh book recommendations
     */
    fun refreshRecommendations() {
        loadRecommendations()
    }
}