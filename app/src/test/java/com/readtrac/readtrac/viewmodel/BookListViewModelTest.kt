package com.readtrac.readtrac.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.readtrac.readtrac.data.model.BookEntity
import com.readtrac.readtrac.data.repository.IBookRepository
import com.readtrac.readtrac.data.entity.Book
import com.readtrac.readtrac.util.anyObject
import com.readtrac.readtrac.util.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito

/**
 * Unit tests for the BookViewModel focusing on BookList functionality
 *
 * Tests the ViewModel methods related to retrieving and displaying the book list
 */
@ExperimentalCoroutinesApi
class BookListViewModelTest {

    // For LiveData testing
    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    // Test dispatcher for coroutines - using UnconfinedTestDispatcher for immediate execution
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    // System under test
    private lateinit var viewModel: BookViewModel

    // Dependencies
    private lateinit var mockRepository: IBookRepository

    // Test data
    private val testBooks = listOf(
        BookEntity(
            id = 1,
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            progress = 0.75f,
            genre = "Fiction"
        ),
        BookEntity(
            id = 2,
            title = "To Kill a Mockingbird",
            author = "Harper Lee",
            progress = 0.3f,
            genre = "Classic"
        ),
        BookEntity(
            id = 3,
            title = "1984",
            author = "George Orwell",
            progress = 0.9f
        )
    )

    @Before
    fun setup() {
        // Set main dispatcher for coroutines
        Dispatchers.setMain(testDispatcher)

        // Create mock dependencies
        mockRepository = Mockito.mock(IBookRepository::class.java)

        // Create ViewModel with mock dependencies
        viewModel = BookViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        // Reset main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun books_returnsTransformedBookList() = runTest {
        // Arrange
        whenever(mockRepository.getAllBooks()).thenReturn(flowOf(testBooks))

        // Act
        val result = viewModel.books.first()

        // Assert
        assertEquals(3, result.size)
        
        // Verify first book
        assertEquals(1L, result[0].id)
        assertEquals("The Great Gatsby", result[0].title)
        assertEquals("F. Scott Fitzgerald", result[0].author)
        assertEquals(0.75f, result[0].progress)
        assertEquals("Fiction", result[0].genre)
        
        // Verify second book
        assertEquals(2L, result[1].id)
        assertEquals("To Kill a Mockingbird", result[1].title)
        assertEquals("Harper Lee", result[1].author)
        assertEquals(0.3f, result[1].progress)
        assertEquals("Classic", result[1].genre)
        
        // Verify third book
        assertEquals(3L, result[2].id)
        assertEquals("1984", result[2].title)
        assertEquals("George Orwell", result[2].author)
        assertEquals(0.9f, result[2].progress)
        assertEquals(null, result[2].genre) // Genre is null for the third book
        
        // Verify repository was called
        Mockito.verify(mockRepository).getAllBooks()
    }
    
    @Test
    fun books_whenRepositoryReturnsEmpty_emptyListReturned() = runTest {
        // Arrange
        whenever(mockRepository.getAllBooks()).thenReturn(flowOf(emptyList()))
        
        // Act
        val result = viewModel.books.first()
        
        // Assert
        assertEquals(0, result.size)
        
        // Verify repository was called
        Mockito.verify(mockRepository).getAllBooks()
    }
    
    @Test
    fun entityToUIModelConversion_correctlyMapsAllFields() {
        // Arrange
        val entity = BookEntity(
            id = 5,
            title = "Test Book",
            author = "Test Author",
            progress = 0.42f,
            genre = "Test Genre",
            notes = "Test Notes",
            rating = 4.5f
        )
        
        // Act
        val uiModel = entity.toUiModel()
        
        // Assert - verify all fields are mapped correctly
        assertEquals(5L, uiModel.id)
        assertEquals("Test Book", uiModel.title)
        assertEquals("Test Author", uiModel.author)
        assertEquals(0.42f, uiModel.progress)
        assertEquals("Test Genre", uiModel.genre)
        assertEquals("Test Notes", uiModel.notes)
        assertEquals(4.5f, uiModel.rating)
    }
    
    /**
     * Helper extension function to convert BookEntity to Book UI model
     * for testing purposes
     */
    private fun BookEntity.toUiModel(): Book {
        return Book(
            id = this.id,
            title = this.title,
            author = this.author,
            progress = this.progress,
            rating = this.rating,
            genre = this.genre,
            notes = this.notes
        )
    }
}