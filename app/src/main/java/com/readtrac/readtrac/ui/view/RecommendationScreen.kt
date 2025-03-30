package com.readtrac.readtrac.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.readtrac.readtrac.data.entity.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(recommendedBooks: List<Book>) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Recommended Books") }) }
    ) { innerPadding ->
        LazyRow(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(recommendedBooks) { book ->
                RecommendationCard(book)
            }
        }
    }
}

@Composable
fun RecommendationCard(book: Book) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = book.title, fontWeight = FontWeight.Bold)
            Text(text = "by ${book.author}")
        }
    }
}