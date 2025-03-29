package com.readtrac.readtrac.routes

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.readtrac.readtrac.MainActivity
import com.readtrac.readtrac.data.repository.BookRepository
import com.readtrac.readtrac.data.repository.IBookRepository
import com.readtrac.readtrac.ui.view.Book
import com.readtrac.readtrac.viewmodel.BookDetailViewModel
import com.readtrac.readtrac.viewmodel.BookViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import javax.inject.Inject

/**
 * Screen-to-screen navigation tests for the ReadTrac app.
 * 
 * These tests focus on the actual user journey between screens,
 * verifying that UI elements and data are correctly displayed
 * after navigation events.
 */
@HiltAndroidTest
class ScreenNavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Inject
    lateinit var bookRepository: IBookRepository
    
    // Test book data
    private val testBook = Book(
        id = 42L,
        title = "The Hitchhiker's Guide to the Galaxy",
        author = "Douglas Adams",
        progress = 0.42f,
        genre = "Science Fiction",
        notes = "Don't Panic!"
    )
    
    @Before
    fun setup() {
        hiltRule.inject()
        
        // Ensure we have at least one book to test with
        runBlocking {
            // Clear any existing books
            (bookRepository as? BookRepository)?.deleteAllBooks()
            
            // Add our test book
            bookRepository.insertBook(
                title = testBook.title, 
                author = testBook.author, 
                notes = testBook.notes ?: "",
                genre = testBook.genre ?: ""
            )
        }
    }
    
    @Test
    fun navigateFromHomeToBookDetailAndVerifyContent() {
        // Start at home screen
        composeTestRule.waitForIdle()
        
        // Verify our test book is displayed
        composeTestRule.onNodeWithText(testBook.title).assertIsDisplayed()
        composeTestRule.onNodeWithText("by ${testBook.author}").assertIsDisplayed()
        
        // Click on the book to navigate to details
        composeTestRule.onNodeWithText(testBook.title).performClick()
        
        // Verify we're on the book details screen and content is displayed correctly
        composeTestRule.onNodeWithText("Book Details").assertIsDisplayed()
        composeTestRule.onNodeWithText(testBook.title).assertIsDisplayed()
        composeTestRule.onNodeWithText("by ${testBook.author}").assertIsDisplayed()
        composeTestRule.onNodeWithText("Synopsis:").assertIsDisplayed()
        
        // Verify the synopsis/notes is correctly passed
        if (testBook.notes != null) {
            composeTestRule.onNodeWithText(testBook.notes!!).assertIsDisplayed()
        }
    }
    
    @Test
    fun navigateToAddReviewScreenFromBookDetail() {
        // Start at home screen
        composeTestRule.waitForIdle()
        
        // Navigate to book detail
        composeTestRule.onNodeWithText(testBook.title).performClick()
        composeTestRule.waitForIdle()
        
        // Click on Add Review button
        composeTestRule.onNodeWithText("Add Review").performClick()
        composeTestRule.waitForIdle()
        
        // Verify we're on the review screen
        composeTestRule.onNodeWithText("Book Reviews").assertIsDisplayed()
        
        // Verify we can add a review (dialog appears when clicking Add Review)
        composeTestRule.onNodeWithContentDescription("Add Review").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Add Review").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your thoughts on this book").assertIsDisplayed()
    }
    
    @Test 
    fun navigateBackFromDetailToHome() {
        // Start at home screen
        composeTestRule.waitForIdle()
        
        // Navigate to book detail
        composeTestRule.onNodeWithText(testBook.title).performClick()
        composeTestRule.waitForIdle()
        
        // Navigate back
        composeTestRule.onNodeWithContentDescription("Navigate back").performClick()
        composeTestRule.waitForIdle()
        
        // Verify we're back at the home screen
        // The book list should be visible again
        composeTestRule.onNodeWithText(testBook.title).assertIsDisplayed()
        composeTestRule.onNodeWithText("by ${testBook.author}").assertIsDisplayed()
    }
}