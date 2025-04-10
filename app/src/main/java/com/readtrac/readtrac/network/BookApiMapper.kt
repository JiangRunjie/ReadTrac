package com.readtrac.readtrac.network

import android.util.Log
import com.readtrac.readtrac.model.BookEntity

/**
 * Mapper class to convert BookApiItem objects to BookEntity objects
 * 
 * This class contains methods to map API response objects to domain entities
 * that can be used throughout the application.
 */
object BookApiMapper {
    
    private const val TAG = "BookApiMapper"
    
    /**
     * Convert a BookApiItem to a BookEntity
     * 
     * @param apiItem The BookApiItem from the API
     * @return A BookEntity for use in the app
     */
    fun mapToBookEntity(apiItem: BookApiItem): BookEntity {
        val volumeInfo = apiItem.volumeInfo
        
        // Fix image URLs - Google Books API returns HTTP urls, but Android requires HTTPS
        val originalCoverUrl = volumeInfo.imageLinks?.thumbnail
        val fixedCoverUrl = originalCoverUrl?.replace("http://", "https://")
        
        // Log for debugging
        Log.d(TAG, "Original cover URL: $originalCoverUrl")
        Log.d(TAG, "Fixed cover URL: $fixedCoverUrl")
        
        return BookEntity(
            id = 0, // Room will generate a new ID
            title = volumeInfo.title,
            author = volumeInfo.authors?.joinToString(", ") ?: "Unknown Author",
            isbn = apiItem.id, // Using the API ID as ISBN
            coverUrl = fixedCoverUrl,
            genre = volumeInfo.categories?.firstOrNull(),
            description = volumeInfo.description,
            rating = volumeInfo.averageRating,
            pageCount = volumeInfo.pageCount?.toInt() ?: 0,
            progress = 0f, // New external books start with 0 progress
            isExternal = true // Marking as external source
        )
    }
    
    /**
     * Convert a list of BookApiItems to BookEntities
     * 
     * @param apiItems List of BookApiItems from the API
     * @return List of BookEntities for use in the app
     */
    fun mapToBookEntities(apiItems: List<BookApiItem>?): List<BookEntity> {
        return apiItems?.map { mapToBookEntity(it) } ?: emptyList()
    }
}