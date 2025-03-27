package com.readtrac.readtrac.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    book: Book,
    onProgressUpdate: (Float) -> Unit,
    onAddReview: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Book Details") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            // Title and Author
            Text(text = book.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = "by ${book.author}", style = MaterialTheme.typography.bodyMedium)

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
            Text(text = "User Reviews:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(onClick = { onAddReview("New Review") }, modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Add Review")
            }

            // Placeholder for reviews
            Text(text = "No reviews yet.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}