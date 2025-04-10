package com.readtrac.readtrac.network

import com.google.gson.annotations.SerializedName

/**
 * Root response object for book search API
 * 
 * This model represents the top-level response from the Google Books API
 */
data class BookSearchResponse(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("totalItems")
    val totalItems: Int,
    @SerializedName("items")
    val items: List<BookApiItem>? = null
)

/**
 * Represents a single book item from the API
 */
data class BookApiItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfo
)

/**
 * Contains detailed information about a book volume
 */
data class VolumeInfo(
    @SerializedName("title")
    val title: String,
    @SerializedName("authors")
    val authors: List<String>? = null,
    @SerializedName("publisher")
    val publisher: String? = null,
    @SerializedName("publishedDate")
    val publishedDate: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("pageCount")
    val pageCount: Int? = null,
    @SerializedName("categories")
    val categories: List<String>? = null,
    @SerializedName("averageRating")
    val averageRating: Float? = null,
    @SerializedName("ratingsCount")
    val ratingsCount: Int? = null,
    @SerializedName("imageLinks")
    val imageLinks: ImageLinks? = null,
    @SerializedName("language")
    val language: String? = null
)

/**
 * Contains links to book cover images
 */
data class ImageLinks(
    @SerializedName("smallThumbnail")
    val smallThumbnail: String? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null
)