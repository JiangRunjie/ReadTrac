package com.readtrac.readtrac.routes

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.readtrac.readtrac.data.repository.IBookRepository
import com.readtrac.readtrac.ui.theme.ReadTracTheme
import com.readtrac.readtrac.data.entity.Book
import com.readtrac.readtrac.viewmodel.BookDetailViewModel
import com.readtrac.readtrac.viewmodel.BookViewModel
import com.readtrac.readtrac.viewmodel.ReviewViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import javax.inject.Inject

/**
 * Integration tests for navigation between screens in the ReadTrac app
 * 
 * These tests verify that:
 * - Navigation from Home Screen to Book Detail Screen works correctly
 * - Book ID is passed correctly from Home Screen to Book Detail Screen
 * - Navigation from Book Detail to Review Screen works correctly
 * - Back navigation between screens works as expected
 */
@HiltAndroidTest
class NavHostIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    // Test NavHostController to verify navigation
    private lateinit var navController: TestNavHostController
    
    // Mock ViewModel dependencies
    private lateinit var mockBookViewModel: BookViewModel
    private lateinit var mockBookDetailViewModel: BookDetailViewModel
    private lateinit var mockReviewViewModel: ReviewViewModel
    
    // Sample test data
    private val testBooks = listOf(
        Book(
            id = 1L,
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            progress = 0.75f,
            genre = "Fiction",
            notes = "A classic novel about the American Dream"
        ),
        Book(
            id = 2L,
            title = "To Kill a Mockingbird",
            author = "Harper Lee",
            progress = 0.3f,
            genre = "Classic",
            notes = "A story about racial injustice"
        )
    )
    
    private val sampleBookDetail = Book(
        id = 1L,
        title = "The Great Gatsby",
        author = "F. Scott Fitzgerald",
        progress = 0.75f,
        genre = "Fiction",
        notes = "A classic novel about the American Dream"
    )

    @Before
    fun setUp() {
        hiltRule.inject()
        
        // Initialize mock ViewModels
        mockBookViewModel = mock()
        mockBookDetailViewModel = mock()
        mockReviewViewModel = mock()
        
        // Setup mock data
        val booksFlow = MutableStateFlow(testBooks)
        whenever(mockBookViewModel.books).thenReturn(booksFlow)
        
        val bookDetailFlow = MutableStateFlow(sampleBookDetail)
        whenever(mockBookDetailViewModel.bookDetail).thenReturn(bookDetailFlow)
    }

    @Test
    fun verifyStartDestination() {
        // Set up the navigation test environment
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            ReadTracTheme {
                AppNavigation()
            }
        }
        
        // Verify that the start destination is the Home screen
        val currentDestination = navController.currentBackStackEntry?.destination?.route
        assert(currentDestination == Screen.Home.route)
    }
    
    @Test
    fun navigateFromHomeToBookDetail() {
        // Set up the navigation test environment
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            ReadTracTheme {
                AppNavigation()
            }
        }
        
        // Wait for home screen to load
        composeTestRule.waitForIdle()
        
        // Find and click on the first book item (The Great Gatsby)
        composeTestRule.onNodeWithText("The Great Gatsby").performClick()
        
        // Wait for navigation to complete
        composeTestRule.waitForIdle()
        
        // Verify navigation to book detail screen with correct book ID
        val currentDestination = navController.currentBackStackEntry?.destination?.route
        assert(currentDestination?.startsWith("book_detail/") == true)
        
        // Verify correct book ID was passed
        val bookId = navController.currentBackStackEntry?.arguments?.getLong("bookId")
        assert(bookId == 1L)
    }
    
    @Test
    fun navigateFromBookDetailToReview() {
        // Set up the navigation test environment and navigate to book detail screen
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            ReadTracTheme {
                AppNavigation()
            }
        }
        
        // Navigate to book detail for the first book
        navController.navigate(Screen.BookDetail.createRoute(1L))
        
        // Wait for book detail screen to load
        composeTestRule.waitForIdle()
        
        // Find and click on the Add Review button
        composeTestRule.onNodeWithText("Add Review").performClick()
        
        // Wait for navigation to complete
        composeTestRule.waitForIdle()
        
        // Verify navigation to review screen with correct book ID
        val currentDestination = navController.currentBackStackEntry?.destination?.route
        assert(currentDestination?.startsWith("review/") == true)
        
        // Verify correct book ID was passed
        val bookId = navController.currentBackStackEntry?.arguments?.getLong("bookId")
        assert(bookId == 1L)
    }
    
    @Test
    fun navigationBackWorksCorrectly() {
        // Set up the navigation test environment
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            ReadTracTheme {
                AppNavigation()
            }
        }
        
        // Navigate to book detail for the first book
        navController.navigate(Screen.BookDetail.createRoute(1L))
        composeTestRule.waitForIdle()
        
        // Navigate back to home
        navController.popBackStack()
        composeTestRule.waitForIdle()
        
        // Verify we're back at the home screen
        val currentDestination = navController.currentBackStackEntry?.destination?.route
        assert(currentDestination == Screen.Home.route)
    }
}