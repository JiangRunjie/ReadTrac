package com.readtrac.readtrac.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.readtrac.readtrac.viewmodel.ReviewViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen that displays reviews for a book and allows adding new reviews
 * 
 * @param bookId ID of the book to show reviews for
 * @param viewModel ViewModel that manages review data
 * @param onReviewSubmitted Callback when a review is submitted
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    bookId: Long,
    viewModel: ReviewViewModel = hiltViewModel(),
    onReviewSubmitted: () -> Unit = {}
) {
    val reviews by viewModel.getReviewsForBook(bookId).collectAsState(initial = emptyList())
    var showAddReviewDialog by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    var isPublicReview by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // Create a coroutine scope

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Reviews") },
                navigationIcon = {
                    IconButton(onClick = onReviewSubmitted) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddReviewDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Review")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            if (reviews.isEmpty()) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        Text(
                            text = "No reviews yet. Be the first to add a review!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(reviews) { review ->
                    ReviewCard(review)
                }
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
                                viewModel.addReview(bookId, reviewText, isPublicReview)
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
}

@Composable
fun ReviewCard(review: Review) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (review.isPublic) {
                    Text(
                        text = "Public Review",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = dateFormatter.format(Date(review.timestamp)),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = review.reviewText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}