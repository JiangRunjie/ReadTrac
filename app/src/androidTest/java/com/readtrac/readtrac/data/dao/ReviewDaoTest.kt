package com.readtrac.readtrac.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.readtrac.readtrac.data.database.AppDatabase
import com.readtrac.readtrac.data.model.BookEntity
import com.readtrac.readtrac.data.model.ReviewEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented tests for ReviewDao operations
 */
@RunWith(AndroidJUnit4::class)
class ReviewDaoTest {
    private lateinit var reviewDao: ReviewDao
    private lateinit var bookDao: BookDao
    private lateinit var database: AppDatabase
    private var testBookId: Long = 0

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        reviewDao = database.reviewDao()
        bookDao = database.bookDao()
        
        // Insert a test book that we'll use for the reviews
        val book = BookEntity(title = "Test Book for Reviews", author = "Test Author")
        testBookId = bookDao.insertBook(book)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetReview() = runBlocking {
        // Create a test review
        val review = ReviewEntity(
            bookId = testBookId,
            reviewText = "This is a test review",
            isPublic = true
        )
        
        // Insert review and get the ID
        val reviewId = reviewDao.insertReview(review)
        
        // Retrieve the review by ID
        val retrievedReview = reviewDao.getReviewById(reviewId)
        
        // Verify review was correctly saved
        assertNotNull(retrievedReview)
        assertEquals("This is a test review", retrievedReview?.reviewText)
        assertEquals(testBookId, retrievedReview?.bookId)
        assertTrue(retrievedReview?.isPublic == true)
    }
    
    @Test
    @Throws(Exception::class)
    fun getReviewsForBook() = runBlocking {
        // Create test reviews for the same book
        val review1 = ReviewEntity(bookId = testBookId, reviewText = "Review 1")
        val review2 = ReviewEntity(bookId = testBookId, reviewText = "Review 2")
        
        // Create a second book and a review for it
        val book2Id = bookDao.insertBook(BookEntity(title = "Another Book", author = "Another Author"))
        val review3 = ReviewEntity(bookId = book2Id, reviewText = "Review for another book")
        
        // Insert all reviews
        reviewDao.insertReview(review1)
        reviewDao.insertReview(review2)
        reviewDao.insertReview(review3)
        
        // Get reviews for first book
        val bookReviews = reviewDao.getReviewsForBook(testBookId).first()
        
        // Verify we got the right reviews
        assertEquals(2, bookReviews.size)
        assertTrue(bookReviews.any { it.reviewText == "Review 1" })
        assertTrue(bookReviews.any { it.reviewText == "Review 2" })
        assertFalse(bookReviews.any { it.reviewText == "Review for another book" })
    }
    
    @Test
    @Throws(Exception::class)
    fun updateReview() = runBlocking {
        // Create and insert a review
        val review = ReviewEntity(
            bookId = testBookId,
            reviewText = "Original review text",
            isPublic = false
        )
        val reviewId = reviewDao.insertReview(review)
        
        // Get the review to update
        val retrievedReview = reviewDao.getReviewById(reviewId)
        requireNotNull(retrievedReview)
        
        // Update the review
        val updatedReview = retrievedReview.copy(
            reviewText = "Updated review text",
            isPublic = true
        )
        reviewDao.updateReview(updatedReview)
        
        // Get the updated review
        val retrievedUpdatedReview = reviewDao.getReviewById(reviewId)
        
        // Verify updates
        assertNotNull(retrievedUpdatedReview)
        assertEquals("Updated review text", retrievedUpdatedReview?.reviewText)
        assertTrue(retrievedUpdatedReview?.isPublic == true)
    }
    
    @Test
    @Throws(Exception::class)
    fun deleteReview() = runBlocking {
        // Create and insert reviews
        val review1 = ReviewEntity(bookId = testBookId, reviewText = "Review 1")
        val review2 = ReviewEntity(bookId = testBookId, reviewText = "Review 2")
        
        val review1Id = reviewDao.insertReview(review1)
        reviewDao.insertReview(review2)
        
        // Verify we have 2 reviews
        assertEquals(2, reviewDao.getAllReviews().first().size)
        
        // Delete review1
        val retrievedReview1 = reviewDao.getReviewById(review1Id)
        requireNotNull(retrievedReview1)
        reviewDao.deleteReview(retrievedReview1)
        
        // Verify we have 1 review remaining
        val remainingReviews = reviewDao.getAllReviews().first()
        assertEquals(1, remainingReviews.size)
        assertEquals("Review 2", remainingReviews[0].reviewText)
    }
    
    @Test
    @Throws(Exception::class)
    fun deleteReviewsByBookId() = runBlocking {
        // Create reviews for our test book
        reviewDao.insertReview(ReviewEntity(bookId = testBookId, reviewText = "Review 1"))
        reviewDao.insertReview(ReviewEntity(bookId = testBookId, reviewText = "Review 2"))
        reviewDao.insertReview(ReviewEntity(bookId = testBookId, reviewText = "Review 3"))
        
        // Create another book and add a review to it
        val book2Id = bookDao.insertBook(BookEntity(title = "Another Book", author = "Another Author"))
        reviewDao.insertReview(ReviewEntity(bookId = book2Id, reviewText = "Review for another book"))
        
        // Verify we have 4 reviews total
        assertEquals(4, reviewDao.getAllReviews().first().size)
        
        // Delete all reviews for test book
        val deletedCount = reviewDao.deleteReviewsByBookId(testBookId)
        
        // Verify the results
        assertEquals(3, deletedCount) // Should have deleted 3 reviews
        
        // Verify we have 1 review remaining (for the other book)
        val remainingReviews = reviewDao.getAllReviews().first()
        assertEquals(1, remainingReviews.size)
        assertEquals("Review for another book", remainingReviews[0].reviewText)
    }
    
    @Test
    @Throws(Exception::class)
    fun cascadeDeleteWhenBookIsDeleted() = runBlocking {
        // Create reviews for our test book
        reviewDao.insertReview(ReviewEntity(bookId = testBookId, reviewText = "Review 1"))
        reviewDao.insertReview(ReviewEntity(bookId = testBookId, reviewText = "Review 2"))
        
        // Create another book and add a review to it
        val book2Id = bookDao.insertBook(BookEntity(title = "Another Book", author = "Another Author"))
        reviewDao.insertReview(ReviewEntity(bookId = book2Id, reviewText = "Review for another book"))
        
        // Verify we have 3 reviews total
        assertEquals(3, reviewDao.getAllReviews().first().size)
        
        // Delete the test book
        val testBook = bookDao.getBookById(testBookId)
        requireNotNull(testBook)
        bookDao.deleteBook(testBook)
        
        // Verify that the related reviews were automatically deleted (cascade)
        val remainingReviews = reviewDao.getAllReviews().first()
        assertEquals(1, remainingReviews.size)
        assertEquals("Review for another book", remainingReviews[0].reviewText)
    }
}