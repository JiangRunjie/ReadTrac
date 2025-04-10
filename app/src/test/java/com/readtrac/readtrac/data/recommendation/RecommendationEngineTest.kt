package com.readtrac.readtrac.data.recommendation

import com.readtrac.readtrac.model.BookEntity
import com.readtrac.readtrac.model.RecommendationEngine
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class RecommendationEngineTest {

    private lateinit var recommendationEngine: RecommendationEngine
    
    // Test data
    private val userBooks = listOf(
        BookEntity(1, "Book 1", "Author A", 1.0f, 5.0f, "Fantasy"),
        BookEntity(2, "Book 2", "Author B", 1.0f, 3.0f, "Sci-Fi"),
        BookEntity(3, "Book 3", "Author A", 0.5f, 4.5f, "Mystery"),
        BookEntity(4, "Book 4", "Author C", 0.2f, 2.0f, "Romance"),
        BookEntity(5, "Book 5", "Author D", 0.0f, null, "Fantasy")
    )
    
    private val availableBooks = listOf(
        // Books the user hasn't read
        BookEntity(6, "Book 6", "Author A", 0.0f, null, "Fantasy"),
        BookEntity(7, "Book 7", "Author B", 0.0f, null, "Sci-Fi"),
        BookEntity(8, "Book 8", "Author E", 0.0f, null, "Mystery"),
        BookEntity(9, "Book 9", "Author F", 0.0f, null, "Romance"),
        BookEntity(10, "Book 10", "Author G", 0.0f, null, "Horror"),
        // Plus all the user books (to mimic a database query)
        BookEntity(1, "Book 1", "Author A", 1.0f, 5.0f, "Fantasy"),
        BookEntity(2, "Book 2", "Author B", 1.0f, 3.0f, "Sci-Fi"),
        BookEntity(3, "Book 3", "Author A", 0.5f, 4.5f, "Mystery"),
        BookEntity(4, "Book 4", "Author C", 0.2f, 2.0f, "Romance"),
        BookEntity(5, "Book 5", "Author D", 0.0f, null, "Fantasy")
    )

    @Before
    fun setup() {
        recommendationEngine = RecommendationEngine()
    }
    
    @Test
    fun `getRecommendations recommends books in preferred genres`() {
        val recommendations = recommendationEngine.getRecommendations(userBooks, availableBooks, 3)
        
        // Check general conditions
        assertNotNull(recommendations)
        assertTrue("Should contain recommendations", recommendations.isNotEmpty())
        assertTrue("Should not recommend books already read", 
            recommendations.none { rec -> userBooks.any { it.id == rec.id } })
        
        // Should prioritize Fantasy and Mystery genres (books rated 4+ stars)
        val recommendedGenres = recommendations.mapNotNull { it.genre }
        assertTrue("Should recommend books in preferred genres", 
            recommendedGenres.contains("Fantasy") || recommendedGenres.contains("Mystery"))
    }
    
    @Test
    fun `getRecommendations recommends books by favorite authors`() {
        val recommendations = recommendationEngine.getRecommendations(userBooks, availableBooks, 3)
        
        // Author A has highly-rated books
        val hasAuthorRecommendation = recommendations.any { it.author == "Author A" }
        assertTrue("Should recommend books by favorite authors", hasAuthorRecommendation)
    }
    
    @Test
    fun `getRecommendations returns empty list if user has no books`() {
        val recommendations = recommendationEngine.getRecommendations(emptyList(), availableBooks, 3)
        assertTrue("Should return empty list when user has no books", recommendations.isEmpty())
    }
    
    @Test
    fun `getRecommendations limits results to specified number`() {
        val limit = 2
        val recommendations = recommendationEngine.getRecommendations(userBooks, availableBooks, limit)
        assertTrue("Should limit recommendations to specified number", recommendations.size <= limit)
    }
    
    @Test
    fun `mapToBookEntities correctly maps database entities to UI entities`() {
        val bookEntities = listOf(
            BookEntity(1, "Test Book", "Test Author", 0.5f, 4.0f, "Test Genre", notes = "Test Notes")
        )
        
        val uiBooks = recommendationEngine.mapToBookEntities(bookEntities)
        
        assertEquals(1, uiBooks.size)
        assertEquals("Test Book", uiBooks[0].title)
        assertEquals("Test Author", uiBooks[0].author)
        assertEquals(0.5f, uiBooks[0].progress)
        assertEquals(4.0f, uiBooks[0].rating)
        assertEquals("Test Genre", uiBooks[0].genre)
        assertEquals("Test Notes", uiBooks[0].notes)
    }
}