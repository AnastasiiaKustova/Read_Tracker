package com.example.readtracker.android.presentation.bookListScreen

interface BookListScreenComponent {
    fun onBackClick()
    fun onBookClick(bookId: String)
}