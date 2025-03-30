package com.readtrac.readtrac.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.readtrac.readtrac.ui.theme.ReadTracTheme
import com.readtrac.readtrac.viewmodel.BookViewModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha
import com.readtrac.readtrac.data.entity.Book

/**
 * Home screen that displays a list of books the user is tracking
 *
 * This screen serves as the main entry point to the application. It displays
 * a list of books that the user is currently tracking, along with their progress.
 * Users can tap on a book to view its details or add a new book.
 *
 * @param viewModel The ViewModel providing the book data
 * @param onAddBook Callback for when the user wants to add a new book
 * @param onBookSelected Callback for when a book is selected
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BookViewModel = hiltViewModel(),
    onAddBook: () -> Unit = {},
    onBookSelected: (Long) -> Unit = {}
) {
    // Collect the books flow as state
    val books by viewModel.books.collectAsState(initial = emptyList())

    // Track loading state from ViewModel
    val isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("ReadTrac Home") },
            )
        },
        floatingActionButton = {
            if (books.isNotEmpty()) {
                FloatingActionButton(onClick = onAddBook) {
                    Icon(Icons.Default.Add, contentDescription = "Add Book")
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (books.isEmpty()) {
            // Show empty state when no books are available
            EmptyBookListState(onAddBook)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding() + 8.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(books, key = { it.id }) { book ->
                    BookCard(
                        book,
                        onClick = { onBookSelected(book.id) },
                        onDelete = { viewModel.deleteBook(book.id) }
                    )
                }
            }
        }
    }
}

/**
 * Card component displaying a book's information
 *
 * @param book The book to display
 * @param onClick Callback for when the card is clicked
 * @param onDelete Callback for when the card is swiped left
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCard(book: Book, onClick: () -> Unit, onDelete: () -> Unit) {
    val isDeleted = remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isDeleted.value) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        finishedListener = {
            if (isDeleted.value) onDelete()
        }
    )

    if (!isDeleted.value) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -50) { // Swipe left
                            isDeleted.value = true
                        }
                    }
                }
                .clickable(onClick = onClick)
                .alpha(alpha),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "by ${book.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (book.genre != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.genre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Progress section with improved visual feedback
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { book.progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${(book.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Optional rating display
                book.rating?.let { rating ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Rating: ",
                            style = MaterialTheme.typography.bodySmall
                        )
                        RatingBar(rating = rating)
                    }
                }
            }
        }
    }
}

/**
 * Composable that displays a visual representation of a rating
 *
 * @param rating The rating value (0-5)
 */
@Composable
fun RatingBar(rating: Float) {
    Row {
        repeat(5) { index ->
            val starColor = if (index < rating) Color(0xFFFFC107) else Color.Gray
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Composable that displays an empty state when no books are available
 *
 * @param onAddBook Callback for when the user wants to add a new book
 */
@Composable
fun EmptyBookListState(onAddBook: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No books yet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start tracking your reading by adding your first book",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddBook,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Your First Book")
        }
    }
}

/**
 * Preview for the HomeScreen with some sample data
 */
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val sampleBooks = listOf(
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

    ReadTracTheme {
        Surface {
            BookListContent(books = sampleBooks)
        }
    }
}

/**
 * Content of the book list screen extracted for preview purposes
 */
@Composable
private fun BookListContent(books: List<Book>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookCard(book, onClick = {}, onDelete = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    ReadTracTheme {
        Surface {
            EmptyBookListState {}
        }
    }
}