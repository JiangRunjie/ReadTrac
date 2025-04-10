package com.readtrac.readtrac.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.readtrac.readtrac.database.AppDatabase
import com.readtrac.readtrac.model.BookEntity
import com.readtrac.readtrac.repository.IBookRepository
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
class BookRepositoryIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var bookRepository: IBookRepository

    private val testBook1 = BookEntity(
        title = "Test Book 1",
        author = "Test Author 1",
        progress = 0.5f,
        genre = "Fiction"
    )

    private val testBook2 = BookEntity(
        title = "Another Book",
        author = "Different Author",
        progress = 0.25f,
        genre = "Non-Fiction"
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetBook() = runBlocking {
        // Insert a book
        val bookId = bookRepository.insertBook(testBook1)

        // Get the book and verify its data
        val retrievedBook = bookRepository.getBookById(bookId)
        assertNotNull(retrievedBook)
        assertEquals(testBook1.title, retrievedBook?.title)
        assertEquals(testBook1.author, retrievedBook?.author)
        assertEquals(testBook1.progress, retrievedBook?.progress)
        assertEquals(testBook1.genre, retrievedBook?.genre)
    }

    @Test
    fun getAllBooks() = runBlocking {
        // Insert test books
        bookRepository.insertBook(testBook1)
        bookRepository.insertBook(testBook2)

        // Get all books and verify
        val books = bookRepository.getAllBooks().first()
        assertEquals(2, books.size)
        assertTrue(books.any { it.title == testBook1.title })
        assertTrue(books.any { it.title == testBook2.title })
    }

    @Test
    fun searchBooks() = runBlocking {
        // Insert test books
        bookRepository.insertBook(testBook1)
        bookRepository.insertBook(testBook2)

        // Search by title
        val resultsByTitle = bookRepository.searchBooks("Another").first()
        assertEquals(1, resultsByTitle.size)
        assertEquals(testBook2.title, resultsByTitle[0].title)

        // Search by author
        val resultsByAuthor = bookRepository.searchBooks("Test Author").first()
        assertEquals(1, resultsByAuthor.size)
        assertEquals(testBook1.title, resultsByAuthor[0].title)
    }

    @Test
    fun getBooksByGenre() = runBlocking {
        // Insert test books
        bookRepository.insertBook(testBook1)
        bookRepository.insertBook(testBook2)

        // Get books by genre
        val fictionBooks = bookRepository.getBooksByGenre("Fiction").first()
        assertEquals(1, fictionBooks.size)
        assertEquals(testBook1.title, fictionBooks[0].title)

        val nonFictionBooks = bookRepository.getBooksByGenre("Non-Fiction").first()
        assertEquals(1, nonFictionBooks.size)
        assertEquals(testBook2.title, nonFictionBooks[0].title)
    }

    @Test
    fun updateReadingProgress() = runBlocking {
        // Insert a book
        val bookId = bookRepository.insertBook(testBook1)

        // Update its progress
        val updateResult = bookRepository.updateReadingProgress(bookId, 0.75f)
        assertTrue(updateResult)

        // Verify the update
        val updatedBook = bookRepository.getBookById(bookId)
        assertEquals(0.75f, updatedBook?.progress)
    }

    @Test
    fun updateReadingProgress_withInvalidId_returnsFalse() = runBlocking {
        // Try to update progress for non-existent book
        val updateResult = bookRepository.updateReadingProgress(999L, 0.5f)
        assertFalse(updateResult)
    }

    @Test
    fun deleteBook() = runBlocking {
        // Insert a book
        val bookId = bookRepository.insertBook(testBook1)
        
        // Verify it exists
        val book = bookRepository.getBookById(bookId)
        assertNotNull(book)
        
        // Delete it
        bookRepository.deleteBook(book!!)
        
        // Verify it's gone
        val deletedBook = bookRepository.getBookById(bookId)
        assertNull(deletedBook)
    }
}