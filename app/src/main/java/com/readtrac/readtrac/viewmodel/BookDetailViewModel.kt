package com.readtrac.readtrac.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readtrac.readtrac.data.model.BookEntity
import com.readtrac.readtrac.data.repository.IBookRepository
import com.readtrac.readtrac.data.entity.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val repository: IBookRepository
) : ViewModel() {

    private val _bookDetail = MutableStateFlow<Book?>(null)
    val bookDetail: StateFlow<Book?> = _bookDetail.asStateFlow()

    fun fetchBookDetails(bookId: Long) {
        viewModelScope.launch {
            val bookEntity = repository.getBookById(bookId)
            _bookDetail.value = bookEntity?.toUiModel()
        }
    }

    fun updateProgress(bookId: Long, progress: Float) {
        viewModelScope.launch {
            repository.updateReadingProgress(bookId, progress)
            fetchBookDetails(bookId) // Refresh details
        }
    }

    private fun BookEntity.toUiModel(): Book {
        return Book(
            id = this.id,
            title = this.title,
            author = this.author,
            progress = this.progress,
            rating = this.rating,
            genre = this.genre,
            notes = this.notes
        )
    }
}