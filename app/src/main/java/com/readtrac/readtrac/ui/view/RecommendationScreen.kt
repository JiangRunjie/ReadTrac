package com.readtrac.readtrac.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.readtrac.readtrac.data.entity.Book
import com.readtrac.readtrac.viewmodel.BookViewModel
import com.readtrac.readtrac.viewmodel.RecommendationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    viewModel: RecommendationViewModel = hiltViewModel(),
    onBookSelected: (Long) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    // Collect UI state from ViewModel
    val recommendedBooks by viewModel.recommendedBooks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    // State to track if a book is selected for details view
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    
    // If a book is selected, show details screen instead of recommendations list
    if (selectedBook != null) {
        RecommendedBookDetailsScreen(
            book = selectedBook!!,
            onBackPressed = { selectedBook = null },
            onAddToCollection = { bookAdded ->
                // After adding to collection, return to recommendations
                selectedBook = null
            }
        )
        return
    }

    // Load recommendations when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadRecommendations()
    }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Book Recommendations") },
                actions = {
                    IconButton(onClick = { 
                        // Fetch a different batch of books when refresh is clicked
                        viewModel.refreshRecommendations(forceNewBatch = true)
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            when {
                // Show loading spinner when initially loading
                isLoading && recommendedBooks.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loading_indicator")
                    )
                }
                
                // Show error message
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Unknown error occurred",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshRecommendations() }) {
                            Text("Try Again")
                        }
                    }
                }
                
                // Show empty state
                recommendedBooks.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No recommendations available",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Rate more books to get personalized recommendations",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Show recommendations with SwipeRefresh
                else -> {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isLoading),
                        onRefresh = {
                            // Use the same callback as the refresh button
                            coroutineScope.launch {
                                viewModel.refreshRecommendations(forceNewBatch = true)
                            }
                        },
                        indicator = { state, refreshTrigger ->
                            SwipeRefreshIndicator(
                                state = state,
                                refreshTriggerDistance = refreshTrigger,
                                backgroundColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    text = "Books you might enjoy",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            
                            items(recommendedBooks) { book ->
                                RecommendationCard(
                                    book = book,
                                    onClick = { 
                                        // Update to use the local state to show details screen
                                        selectedBook = book
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationCard(
    book: Book,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book cover image with placeholder background when not available
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // Display book cover image using AsyncImage with proper content scaling
                book.coverUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Book cover for ${book.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Book info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "by ${book.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Show genre if available
                book.genre?.let { genre ->
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Show rating if available
                book.rating?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            val starColor = if (index < rating) 
                                Color(0xFFFFB400) else Color.Gray.copy(alpha = 0.3f)
                            
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = starColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                // External indicator removed as requested
            }
        }
    }
}

/**
 * Screen for displaying detailed information about a recommended book
 * with the option to add it to the user's collection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedBookDetailsScreen(
    book: Book,
    bookViewModel: BookViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onAddToCollection: (Book) -> Unit
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var isAddingToCollection by remember { mutableStateOf(false) }
    var isAddedSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Book cover and title section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Book cover image
                book.coverUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Cover of ${book.title}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
            
            // Book info section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "by ${book.author}",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Genre
                book.genre?.let { genre ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Genre: ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = genre,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                // Rating display
                book.rating?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Rating: ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row {
                            repeat(5) { index ->
                                val starColor = if (index < rating) 
                                    Color(0xFFFFB400) else Color.Gray.copy(alpha = 0.3f)
                                
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = starColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                
                Divider()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description or notes if available
                book.notes?.let { notes ->
                    if (notes.isNotEmpty()) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = notes,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        Divider()
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                // Add to Collection button
                Button(
                    onClick = {
                        isAddingToCollection = true
                        scope.launch {
                            try {
                                bookViewModel.addBook(book.title, book.author)
                                isAddedSuccess = true
                                // Wait for a short time to show success message
                                kotlinx.coroutines.delay(1500)
                                onAddToCollection(book)
                            } catch (e: Exception) {
                                isAddingToCollection = false
                            }
                        }
                    },
                    enabled = !isAddingToCollection && !isAddedSuccess,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isAddingToCollection) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        } else if (isAddedSuccess) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        
                        Text(
                            text = when {
                                isAddedSuccess -> "Added to Collection"
                                isAddingToCollection -> "Adding..."
                                else -> "Add to My Collection"
                            }
                        )
                    }
                }
                
                if (isAddedSuccess) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Book successfully added to your collection!",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}