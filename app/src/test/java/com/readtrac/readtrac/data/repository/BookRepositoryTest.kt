package com.readtrac.readtrac.data.repository

import com.readtrac.readtrac.data.dao.BookDao
import com.readtrac.readtrac.data.model.BookEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Unit tests for the BookRepository class
 */
class BookRepositoryTest {

    // Mock dependencies
    private lateinit var mockBookDao: BookDao
    private lateinit var bookRepository: BookRepository

    @Before
    fun setUp() {
        // Create mock DAO
        mockBookDao = mock(BookDao::class.java)
        
        // Create repository with mock dependencies
        bookRepository = BookRepository(mockBookDao)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getAllBooks calls dao getAllBooks`() = runTest {
        // Given
        val testBooks = listOf(
            BookEntity(id = 1, title = "Test Book 1", author = "Author 1", progress = 0.5f),
            BookEntity(id = 2, title = "Test Book 2", author = "Author 2", progress = 0.3f)
        )
        `when`(mockBookDao.getAllBooks()).thenReturn(flowOf(testBooks))

        // When
        bookRepository.getAllBooks()

        // Then
        verify(mockBookDao).getAllBooks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getBookById calls dao getBookById`() = runTest {
        // Given
        val bookId = 1L
        val testBook = BookEntity(id = bookId, title = "Test Book", author = "Test Author", progress = 0.5f)
        `when`(mockBookDao.getBookById(bookId)).thenReturn(testBook)

        // When
        val result = bookRepository.getBookById(bookId)

        // Then
        verify(mockBookDao).getBookById(bookId)
        assert(result == testBook)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test insertBook calls dao insertBook`() = runTest {
        // Given
        val testBook = BookEntity(title = "New Book", author = "New Author", progress = 0f)
        val expectedId = 1L
        `when`(mockBookDao.insertBook(testBook)).thenReturn(expectedId)

        // When
        val result = bookRepository.insertBook(testBook)

        // Then
        verify(mockBookDao).insertBook(testBook)
        assert(result == expectedId)
    }
}