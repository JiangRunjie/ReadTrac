package com.readtrac.readtrac.ui.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.readtrac.readtrac.data.entity.Book
import com.readtrac.readtrac.data.repository.IBookRepository
import com.readtrac.readtrac.ui.theme.ReadTracTheme
import com.readtrac.readtrac.viewmodel.BookViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * UI tests for the BookListScreen (HomeScreen)
 * 
 * These tests verify that the HomeScreen displays books correctly
 * and handles user interactions as expected.
 */
class BookListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mock dependencies
    private lateinit var mockBookRepository: IBookRepository
    private lateinit var bookViewModel: BookViewModel

    // Test data
    private val testBooks = listOf(
        Book(
            id = 1,
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            progress = 0.75f,
            genre = "Fiction",
            rating = 4.5f
        ),
        Book(
            id = 2,
            title = "To Kill a Mockingbird",
            author = "Harper Lee",
            progress = 0.3f,
            genre = "Classic"
        ),
        Book(
            id = 3,
            title = "1984",
            author = "George Orwell",
            progress = 0.9f,
            rating = 5f
        )
    )
    
    private val emptyBookList = emptyList<Book>()
    
    // Track interactions
    private var bookSelectedId: Long? = null
    private var addBookClicked = false

    @Before
    fun setup() {
        // Initialize mocks and test data
        mockBookRepository = mock()
        bookViewModel = BookViewModel(mockBookRepository)
    }

    @Test
    fun booksDisplayedCorrectly() {
        // Set up the ViewModel with test data
        val booksFlow = MutableStateFlow(testBooks)
        whenever(bookViewModel.books).thenReturn(booksFlow)
        
        // Launch the UI
        composeTestRule.setContent {
            ReadTracTheme {
                HomeScreen(
                    viewModel = bookViewModel,
                    onAddBook = { addBookClicked = true },
                    onBookSelected = { bookSelectedId = it }
                )
            }
        }
        
        // Verify books are displayed
        composeTestRule.onNodeWithText("The Great Gatsby").assertIsDisplayed()
        composeTestRule.onNodeWithText("by F. Scott Fitzgerald").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fiction").assertIsDisplayed()
        composeTestRule.onNodeWithText("75%").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("To Kill a Mockingbird").assertIsDisplayed()
        composeTestRule.onNodeWithText("by Harper Lee").assertIsDisplayed()
        
        composeTestRule.onNodeWithText("1984").assertIsDisplayed()
        composeTestRule.onNodeWithText("by George Orwell").assertIsDisplayed()
    }
    
    @Test
    fun emptyStateDisplayedWhenNoBooks() {
        // Set up the ViewModel with empty data
        val booksFlow = MutableStateFlow(emptyBookList)
        whenever(bookViewModel.books).thenReturn(booksFlow)
        
        // Launch the UI
        composeTestRule.setContent {
            ReadTracTheme {
                HomeScreen(
                    viewModel = bookViewModel,
                    onAddBook = { addBookClicked = true }
                )
            }
        }
        
        // Verify empty state is displayed
        composeTestRule.onNodeWithText("No books yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start tracking your reading by adding your first book").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Your First Book").assertIsDisplayed()
    }
    
    @Test
    fun bookClickNavigatesToDetails() {
        // Set up the ViewModel with test data
        val booksFlow = MutableStateFlow(testBooks)
        whenever(bookViewModel.books).thenReturn(booksFlow)
        
        // Launch the UI
        composeTestRule.setContent {
            ReadTracTheme {
                HomeScreen(
                    viewModel = bookViewModel,
                    onBookSelected = { bookSelectedId = it }
                )
            }
        }
        
        // Click on a book
        composeTestRule.onNodeWithText("The Great Gatsby").performClick()
        
        // Verify the correct book ID is passed to the navigation callback
        assert(bookSelectedId == 1L)
    }
    
    @Test
    fun addBookButtonOpensAddBookScreen() {
        // Set up the ViewModel with test data
        val booksFlow = MutableStateFlow(testBooks)
        whenever(bookViewModel.books).thenReturn(booksFlow)
        
        // Launch the UI
        composeTestRule.setContent {
            ReadTracTheme {
                HomeScreen(
                    viewModel = bookViewModel,
                    onAddBook = { addBookClicked = true }
                )
            }
        }
        
        // Click on the add book button in the top app bar
        composeTestRule.onNodeWithContentDescription("Add Book").performClick()
        
        // Verify the add book callback was triggered
        assert(addBookClicked)
    }
}