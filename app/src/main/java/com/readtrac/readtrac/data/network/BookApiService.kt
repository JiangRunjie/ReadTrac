package com.readtrac.readtrac.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for the Google Books API service
 * 
 * This interface defines the API endpoints for interacting with the Google Books API.
 * It uses Retrofit annotations to define HTTP methods, URLs, and query parameters.
 */
interface BookApiService {
    
    /**
     * Search for books
     * 
     * @param query The search query string
     * @param maxResults Maximum number of results to return (default: 10)
     * @return Response containing BookSearchResponse
     */
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10
    ): Response<BookSearchResponse>
    
    /**
     * Get book recommendations
     * 
     * @param categories Categories to use for recommendations (comma-separated)
     * @param maxResults Maximum number of results to return (default: 5)
     * @return Response containing BookSearchResponse
     */
    @GET("volumes")
    suspend fun getRecommendations(
        @Query("q") categories: String,
        @Query("maxResults") maxResults: Int = 5
    ): Response<BookSearchResponse>
}