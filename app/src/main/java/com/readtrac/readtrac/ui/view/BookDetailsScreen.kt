package com.readtrac.readtrac.ui.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.readtrac.readtrac.data.entity.Book
import com.readtrac.readtrac.data.entity.Review
import com.readtrac.readtrac.viewmodel.ReviewViewModel
import com.readtrac.readtrac.viewmodel.BookDetailViewModel
import kotlinx.coroutines.launch

/**
 * Composable that displays a star rating bar 
 * 
 * @param rating Current rating value (0f-5f)
 * @param onRatingChanged Callback for when rating is updated
 * @param modifier Modifier for styling
 * @param enabled Whether the rating bar is interactive
 */
@SuppressLint("DefaultLocale")
@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Float = 24f,
    enabled: Boolean = true
) {
    var hoveredRating by remember { mutableFloatStateOf(-1f) }
    val displayRating = if (hoveredRating >= 0) hoveredRating else rating
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val starFilled = i <= displayRating
            
            IconButton(
                onClick = { if (enabled) onRatingChanged(i.toFloat()) },
                modifier = Modifier.size(starSize.dp)
            ) {
                Icon(
                    imageVector = if (starFilled) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Rate $i star",
                    tint = if (starFilled) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(starSize.dp)
                )
            }
        }
        
        // Display numeric rating next to stars
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (rating > 0) String.format("%.1f", rating) else "Not rated",
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    book: Book,
    onProgressUpdate: (Float) -> Unit,
    onAddReview: (String) -> Unit,
    onBackPressed: () -> Unit = {},
    onRatingChanged: (Float) -> Unit = {},
    reviewViewModel: ReviewViewModel = hiltViewModel(),
    bookDetailViewModel: BookDetailViewModel = hiltViewModel()
) {
    // Fetch reviews for this book
    val reviews by reviewViewModel.getReviewsForBook(book.id).collectAsState(initial = emptyList())
    
    // State for review dialog
    var showAddReviewDialog by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    var isPublicReview by remember { mutableStateOf(false) }
    var reviewRating by remember(book.rating) { mutableFloatStateOf(book.rating ?: 0f) }
    
    // State for delete confirmation dialog
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var reviewToDelete by remember { mutableStateOf<Review?>(null) }
    
    // Coroutine scope for launching suspend functions
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            item {
                // Title and Author
                Text(text = book.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(text = "by ${book.author}", style = MaterialTheme.typography.bodyMedium)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Rating section
                Text(text = "Rating:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                RatingBar(
                    rating = book.rating ?: 0f,
                    onRatingChanged = { newRating ->
                        onRatingChanged(newRating)
                        bookDetailViewModel.updateRating(book.id, newRating)
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Synopsis
                Text(text = "Synopsis:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = book.notes ?: "No synopsis available.", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // Reading Progress
                Text(text = "Reading Progress:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                    progress = { book.progress },
                    modifier = Modifier.weight(1f).height(8.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "${(book.progress * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = book.progress,
                    onValueChange = onProgressUpdate,
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // User Reviews
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "User Reviews:", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { showAddReviewDialog = true }, 
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text("Add Review")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Display reviews or empty state
            if (reviews.isEmpty()) {
                item {
                    Text(
                        text = "No reviews yet. Be the first to add a review!",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(reviews) { review ->
                    ReviewCard(
                        review = review,
                        showDeleteButton = true,
                        onDeleteClick = {
                            reviewToDelete = review
                            showDeleteConfirmDialog = true
                        }
                    )
                }
            }
        }
        
        // Add Review Dialog
        if (showAddReviewDialog) {
            AlertDialog(
                onDismissRequest = { showAddReviewDialog = false },
                title = { Text("Add Review") },
                text = {
                    Column {
                        Text(
                            text = "Rate this book:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Rating Bar in the dialog
                        RatingBar(
                            rating = reviewRating,
                            onRatingChanged = { reviewRating = it },
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            label = { Text("Your thoughts on this book") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Checkbox(
                                checked = isPublicReview,
                                onCheckedChange = { isPublicReview = it }
                            )
                            Text("Make review public")
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (reviewText.isNotBlank()) {
                                // Launch a coroutine to call the suspend function
                                coroutineScope.launch {
                                    // Add the review
                                    reviewViewModel.addReview(book.id, reviewText, isPublicReview)
                                    
                                    // Update book rating if it changed
                                    bookDetailViewModel.updateRating(book.id, reviewRating)
                                    
                                    // Reset and close the dialog
                                    showAddReviewDialog = false
                                    reviewText = ""
                                    isPublicReview = false
                                }
                            }
                        }
                    ) {
                        Text("Submit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddReviewDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Delete Confirmation Dialog
        if (showDeleteConfirmDialog && reviewToDelete != null) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteConfirmDialog = false 
                    reviewToDelete = null
                },
                title = { Text("Delete Review") },
                text = { Text("Are you sure you want to delete this review?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            reviewToDelete?.let { review ->
                                coroutineScope.launch {
                                    reviewViewModel.deleteReview(review.id)
                                    showDeleteConfirmDialog = false
                                    reviewToDelete = null
                                }
                            }
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showDeleteConfirmDialog = false
                            reviewToDelete = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}