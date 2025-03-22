package com.readtrac.readtrac.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.readtrac.readtrac.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BookViewModel = hiltViewModel(),
    onAddBook: () -> Unit = {},
    onBookSelected: (Long) -> Unit = {}
) {
    val books by viewModel.books.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("ReadTrac Home") },
                actions = {
                    IconButton(onClick = onAddBook) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Book")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(books) { book ->
                BookCard(book, onClick = { onBookSelected(book.id) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyMedium
            )
            if (book.genre != null) {
                Text(
                    text = book.genre,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            LinearProgressIndicator(
                progress = book.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Text(
                text = "${(book.progress * 100).toInt()}% complete",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}