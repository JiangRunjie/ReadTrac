package com.readtrac.readtrac.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.readtrac.readtrac.database.AppDatabase
import com.readtrac.readtrac.model.BookEntity
import com.readtrac.readtrac.model.dao.BookDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented tests for BookDao operations
 */
@RunWith(AndroidJUnit4::class)
class BookDaoTest {
    private lateinit var bookDao: BookDao
    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        bookDao = database.bookDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetBook() = runBlocking {
        // Create a test book
        val book = BookEntity(
            title = "Test Book",
            author = "Test Author",
            progress = 0.5f,
            rating = 4.5f
        )
        
        // Insert book and get the ID
        val bookId = bookDao.insertBook(book)
        
        // Retrieve the book by ID
        val retrievedBook = bookDao.getBookById(bookId)
        
        // Verify book was correctly saved
        assertNotNull(retrievedBook)
        assertEquals("Test Book", retrievedBook?.title)
        assertEquals("Test Author", retrievedBook?.author)
        assertEquals(0.5f, retrievedBook?.progress)
        assertEquals(4.5f, retrievedBook?.rating)
    }
    
    @Test
    @Throws(Exception::class)
    fun getAllBooks() = runBlocking {
        // Create test books
        val book1 = BookEntity(title = "Book 1", author = "Author 1")
        val book2 = BookEntity(title = "Book 2", author = "Author 2")
        val book3 = BookEntity(title = "Book 3", author = "Author 3")
        
        // Insert all books
        bookDao.insertBook(book1)
        bookDao.insertBook(book2)
        bookDao.insertBook(book3)
        
        // Get all books
        val allBooks = bookDao.getAllBooks().first()
        
        // Verify number of books
        assertEquals(3, allBooks.size)
        
        // Verify book titles are present
        val titles = allBooks.map { it.title }
        assertTrue(titles.contains("Book 1"))
        assertTrue(titles.contains("Book 2"))
        assertTrue(titles.contains("Book 3"))
    }
    
    @Test
    @Throws(Exception::class)
    fun updateBook() = runBlocking {
        // Create and insert a book
        val book = BookEntity(title = "Original Title", author = "Original Author")
        val bookId = bookDao.insertBook(book)
        
        // Get the book to update
        val retrievedBook = bookDao.getBookById(bookId)
        requireNotNull(retrievedBook)
        
        // Update the book
        val updatedBook = retrievedBook.copy(
            title = "Updated Title",
            progress = 0.75f,
            notes = "Added some notes"
        )
        bookDao.updateBook(updatedBook)
        
        // Get the updated book
        val retrievedUpdatedBook = bookDao.getBookById(bookId)
        
        // Verify updates
        assertNotNull(retrievedUpdatedBook)
        assertEquals("Updated Title", retrievedUpdatedBook?.title)
        assertEquals("Original Author", retrievedUpdatedBook?.author) // Shouldn't change
        assertEquals(0.75f, retrievedUpdatedBook?.progress)
        assertEquals("Added some notes", retrievedUpdatedBook?.notes)
    }
    
    @Test
    @Throws(Exception::class)
    fun deleteBook() = runBlocking {
        // Create and insert books
        val book1 = BookEntity(title = "Book 1", author = "Author 1")
        val book2 = BookEntity(title = "Book 2", author = "Author 2")
        
        val book1Id = bookDao.insertBook(book1)
        bookDao.insertBook(book2)
        
        // Verify we have 2 books
        assertEquals(2, bookDao.getAllBooks().first().size)
        
        // Delete book1
        val retrievedBook1 = bookDao.getBookById(book1Id)
        requireNotNull(retrievedBook1)
        bookDao.deleteBook(retrievedBook1)
        
        // Verify we have 1 book remaining
        val remainingBooks = bookDao.getAllBooks().first()
        assertEquals(1, remainingBooks.size)
        assertEquals("Book 2", remainingBooks[0].title)
    }
}