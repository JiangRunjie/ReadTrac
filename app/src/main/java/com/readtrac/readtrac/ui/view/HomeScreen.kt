package com.readtrac.readtrac.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Data model for a book
data class Book(val title: String, val author: String, val progress: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(books: List<Book>) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("ReadTrac Home")},
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
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
                BookCard(book)
            }
        }
    }
}

@Composable
fun BookCard(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = book.title, fontWeight = FontWeight.Bold)
            Text(text = "by ${book.author}")
            Text(text = "Progress: ${(book.progress * 100).toInt()}%")
        }
    }
}