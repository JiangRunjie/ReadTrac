package com.readtrac.readtrac.data.recommendation

import com.readtrac.readtrac.data.entity.Book
import com.readtrac.readtrac.data.model.BookEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Book recommendation engine that suggests books based on user's reading history.
 *
 * This implementation provides a simple recommendation algorithm based on genres,
 * authors, and ratings from the user's reading history.
 */
@Singleton
class RecommendationEngine @Inject constructor() {
    
    /**
     * Generates book recommendations based on a user's reading history.
     * 
     * The algorithm follows these priority rules:
     * 1. Books with the same genre as highly-rated books (4-5 stars)
     * 2. Books by the same authors as highly-rated books
     * 3. Popular books in genres the user has read
     * 
     * @param userBooks The list of books the user has in their library
     * @param availableBooks The list of all available books (could be from an external API)
     * @param limit Maximum number of recommendations to return
     * @return List of recommended books
     */
    fun getRecommendations(
        userBooks: List<BookEntity>,
        availableBooks: List<BookEntity>,
        limit: Int = 5
    ): List<BookEntity> {
        if (userBooks.isEmpty()) {
            return emptyList()
        }
        
        // For the initial implementation, we'll just use the books in the user's library
        // and pretend they are external book suggestions
        
        // Get the user's preferred genres (from books rated 4+ stars)
        val preferredGenres = userBooks
            .filter { it.rating != null && it.rating >= 4.0f && it.genre != null }
            .mapNotNull { it.genre }
            .toSet()
            
        // Get the user's favorite authors (from books rated 4+ stars)
        val favoriteAuthors = userBooks
            .filter { it.rating != null && it.rating >= 4.0f }
            .map { it.author }
            .toSet()
            
        // Books the user has already read (to exclude from recommendations)
        val readBookIds = userBooks.map { it.id }.toSet()
        
        // Recommendation selection logic
        val recommendations = mutableListOf<BookEntity>()
        
        // 1. First priority: Books in preferred genres
        if (preferredGenres.isNotEmpty()) {
            availableBooks
                .filter { it.id !in readBookIds && it.genre in preferredGenres }
                .take(limit - recommendations.size)
                .forEach { recommendations.add(it) }
        }
        
        // 2. Second priority: Books by favorite authors
        if (recommendations.size < limit && favoriteAuthors.isNotEmpty()) {
            availableBooks
                .filter { 
                    it.id !in readBookIds && 
                    it.id !in recommendations.map { rec -> rec.id } &&
                    it.author in favoriteAuthors 
                }
                .take(limit - recommendations.size)
                .forEach { recommendations.add(it) }
        }
        
        // 3. Third priority: Any genres the user has read
        val allGenres = userBooks.mapNotNull { it.genre }.toSet()
        if (recommendations.size < limit && allGenres.isNotEmpty()) {
            availableBooks
                .filter { 
                    it.id !in readBookIds && 
                    it.id !in recommendations.map { rec -> rec.id } &&
                    it.genre in allGenres 
                }
                .take(limit - recommendations.size)
                .forEach { recommendations.add(it) }
        }
        
        // 4. If still not enough, add some highly-rated books
        if (recommendations.size < limit) {
            availableBooks
                .filter { 
                    it.id !in readBookIds && 
                    it.id !in recommendations.map { rec -> rec.id } 
                }
                .sortedByDescending { it.rating ?: 0f }
                .take(limit - recommendations.size)
                .forEach { recommendations.add(it) }
        }
        
        return recommendations
    }
    
    /**
     * Maps BookEntity objects to Book entities for UI display
     */
    fun mapToBookEntities(bookEntities: List<BookEntity>): List<Book> {
        return bookEntities.map { entity ->
            Book(
                id = entity.id,
                title = entity.title,
                author = entity.author,
                progress = entity.progress,
                rating = entity.rating,
                genre = entity.genre,
                notes = entity.notes
            )
        }
    }
}