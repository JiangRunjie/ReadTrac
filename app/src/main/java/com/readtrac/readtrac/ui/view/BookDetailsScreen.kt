package com.readtrac.readtrac.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(book: Book) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Book Details") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text(text = book.title, fontWeight = FontWeight.Bold)
            Text(text = "by ${book.author}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Book Summary: Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
            progress = { book.progress },
            modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Notes: This section can be used for user's notes on the book.")
        }
    }
}