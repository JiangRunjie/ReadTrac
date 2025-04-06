package com.readtrac.readtrac.ui.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.readtrac.readtrac.data.entity.Book
import com.readtrac.readtrac.data.model.BookEntity
import com.readtrac.readtrac.data.recommendation.RecommendationEngine
import com.readtrac.readtrac.data.repository.IBookRepository
import com.readtrac.readtrac.viewmodel.RecommendationViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecommendationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var bookRepository: IBookRepository
    private lateinit var recommendationEngine: RecommendationEngine
    private lateinit var viewModel: RecommendationViewModel
    
    private val testBooks = listOf(
        Book(1, "Test Book 1", "Author A", 0.5f, 4.5f, genre = "Fantasy"),
        Book(2, "Test Book 2", "Author B", 0.2f, 3.0f, genre = "Sci-Fi"),
        Book(3, "Test Book 3", "Author A", 0.0f, 5.0f, genre = "Fantasy")
    )
    
    @Before
    fun setUp() {
        // Mock dependencies
        bookRepository = mockk(relaxed = true)
        recommendationEngine = mockk(relaxed = true)
        
        // Setup viewModel with mocked dependencies
        viewModel = RecommendationViewModel(bookRepository, recommendationEngine)
        
        // Setup mock behavior
        every { recommendationEngine.mapToBookEntities(any()) } returns testBooks
    }
    
    @Test
    fun recommendationScreen_DisplaysRecommendedBooks() {
        // Set up the recommendation flow to emit our test books
        val mockFlow = flowOf(emptyList()) // content doesn't matter as we're mocking mapToBookEntities
        coEvery { bookRepository.getRecommendedBooks(any()) } returns mockFlow
        
        // Launch the RecommendationScreen
        composeTestRule.setContent {
            RecommendationScreen(
                viewModel = viewModel,
                onBookSelected = {},
                onBackPressed = {}
            )
        }
        
        // Verify the screen title is displayed
        composeTestRule.onNodeWithText("Book Recommendations").assertIsDisplayed()
        
        // Wait for the loading state to be processed
        composeTestRule.waitForIdle()
        
        // Verify that recommendations are displayed
        composeTestRule.onNodeWithText("Based on your reading history").assertIsDisplayed()
        
        // Check that our test books are displayed
        testBooks.forEach { book ->
            composeTestRule.onNodeWithText(book.title).assertIsDisplayed()
            composeTestRule.onNodeWithText("by ${book.author}").assertIsDisplayed()
        }
    }
    
    @Test
    fun recommendationScreen_DisplaysLoadingState() {
        // Create a custom mocked viewModel for this test
        val loadingViewModel = mockk<RecommendationViewModel>(relaxed = true)
        
        // Set up the isLoading state
        val loadingState = MutableStateFlow(true)
        val booksState = MutableStateFlow<List<Book>>(emptyList())
        val errorState = MutableStateFlow<String?>(null)
        
        // Mock the state flows
        every { loadingViewModel.isLoading } returns loadingState
        every { loadingViewModel.recommendedBooks } returns booksState
        every { loadingViewModel.errorMessage } returns errorState
        
        // Launch the RecommendationScreen with our custom viewModel
        composeTestRule.setContent {
            RecommendationScreen(
                viewModel = loadingViewModel,
                onBookSelected = {},
                onBackPressed = {}
            )
        }
        
        // Verify loading indicator is shown
        composeTestRule.onNode(hasTestTag("loading_indicator")).assertExists()
    }
    
    @Test
    fun recommendationScreen_DisplaysEmptyState_WhenNoRecommendations() {
        // Custom mock viewModel for this test
        val emptyViewModel = mockk<RecommendationViewModel>(relaxed = true)
        
        // Set up the states
        val loadingState = MutableStateFlow(false)
        val booksState = MutableStateFlow<List<Book>>(emptyList())
        val errorState = MutableStateFlow<String?>(null)
        
        // Mock the state flows
        every { emptyViewModel.isLoading } returns loadingState
        every { emptyViewModel.recommendedBooks } returns booksState
        every { emptyViewModel.errorMessage } returns errorState
        
        // Launch the RecommendationScreen with our custom viewModel
        composeTestRule.setContent {
            RecommendationScreen(
                viewModel = emptyViewModel,
                onBookSelected = {},
                onBackPressed = {}
            )
        }
        
        // Verify empty state is displayed
        composeTestRule.onNodeWithText("No recommendations available").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rate more books to get personalized recommendations").assertIsDisplayed()
    }
}