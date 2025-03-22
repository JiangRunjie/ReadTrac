package com.readtrac.readtrac.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.readtrac.readtrac.data.database.AppDatabase
import com.readtrac.readtrac.data.model.BookEntity
import com.readtrac.readtrac.data.model.ReviewEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ReviewRepositoryIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var reviewRepository: IReviewRepository

    @Inject
    lateinit var bookRepository: IBookRepository

    private lateinit var testBookId: Long
    private lateinit var testReview1: ReviewEntity
    private lateinit var testReview2: ReviewEntity

    @Before
    fun setup() = runBlocking {
        hiltRule.inject()
        
        // Create a test book first
        val book = BookEntity(
            title = "Test Book",
            author = "Test Author"
        )
        testBookId = bookRepository.insertBook(book)
        
        // Create test reviews with correct bookId
        testReview1 = ReviewEntity(
            bookId = testBookId,
            reviewText = "Great book!",
            isPublic = true
        )
        testReview2 = ReviewEntity(
            bookId = testBookId,
            reviewText = "Could be better",
            isPublic = false
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetReview() = runBlocking {
        // Insert a review
        val reviewId = reviewRepository.insertReview(testReview1)

        // Get the review and verify its data
        val retrievedReview = reviewRepository.getReviewById(reviewId)
        assertNotNull(retrievedReview)
        assertEquals(testReview1.reviewText, retrievedReview?.reviewText)
        assertEquals(testReview1.bookId, retrievedReview?.bookId)
        assertEquals(testReview1.isPublic, retrievedReview?.isPublic)
    }

    @Test
    fun getAllReviews() = runBlocking {
        // Insert test reviews
        reviewRepository.insertReview(testReview1)
        reviewRepository.insertReview(testReview2)

        // Get all reviews and verify
        val reviews = reviewRepository.getAllReviews().first()
        assertEquals(2, reviews.size)
        assertTrue(reviews.any { it.reviewText == testReview1.reviewText })
        assertTrue(reviews.any { it.reviewText == testReview2.reviewText })
    }

    @Test
    fun getReviewsForBook() = runBlocking {
        // Insert test reviews
        reviewRepository.insertReview(testReview1)
        reviewRepository.insertReview(testReview2)

        // Create another book and review
        val otherBookId = bookRepository.insertBook(
            BookEntity(title = "Other Book", author = "Other Author")
        )
        val otherReview = ReviewEntity(
            bookId = otherBookId,
            reviewText = "Review for other book",
            isPublic = true
        )
        reviewRepository.insertReview(otherReview)

        // Get reviews for first book
        val bookReviews = reviewRepository.getReviewsForBook(testBookId).first()
        assertEquals(2, bookReviews.size)
        assertTrue(bookReviews.all { it.bookId == testBookId })
    }

    @Test
    fun getPublicReviews() = runBlocking {
        // Insert test reviews
        reviewRepository.insertReview(testReview1) // public
        reviewRepository.insertReview(testReview2) // private

        // Get public reviews
        val publicReviews = reviewRepository.getPublicReviews().first()
        assertEquals(1, publicReviews.size)
        assertEquals(testReview1.reviewText, publicReviews[0].reviewText)
        assertTrue(publicReviews[0].isPublic)
    }

    @Test
    fun updateReview() = runBlocking {
        // Insert a review
        val reviewId = reviewRepository.insertReview(testReview1)
        val review = reviewRepository.getReviewById(reviewId)
        requireNotNull(review)

        // Update the review
        val updatedReview = review.copy(
            reviewText = "Updated review text",
            isPublic = false
        )
        reviewRepository.updateReview(updatedReview)

        // Verify the update
        val retrievedReview = reviewRepository.getReviewById(reviewId)
        assertEquals("Updated review text", retrievedReview?.reviewText)
        assertFalse(retrievedReview?.isPublic ?: true)
    }

    @Test
    fun deleteReview() = runBlocking {
        // Insert a review
        val reviewId = reviewRepository.insertReview(testReview1)
        val review = reviewRepository.getReviewById(reviewId)
        requireNotNull(review)

        // Delete the review
        reviewRepository.deleteReview(review)

        // Verify it's gone
        val deletedReview = reviewRepository.getReviewById(reviewId)
        assertNull(deletedReview)
    }

    @Test
    fun deleteReviewsByBookId() = runBlocking {
        // Insert test reviews
        reviewRepository.insertReview(testReview1)
        reviewRepository.insertReview(testReview2)

        // Delete reviews for the book
        val deletedCount = reviewRepository.deleteReviewsByBookId(testBookId)

        // Verify deletion
        assertEquals(2, deletedCount)
        val remainingReviews = reviewRepository.getReviewsForBook(testBookId).first()
        assertTrue(remainingReviews.isEmpty())
    }
}