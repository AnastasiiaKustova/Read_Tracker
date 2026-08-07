package com.example.readtracker.android.presentation.bookListScreen

import kotlinx.coroutines.flow.StateFlow

interface BookListScreenComponent {
    val model: StateFlow<BookListScreenStore.State>

    fun onBackClick()

    fun onBookClick(bookId: String)

    fun onMultiSelectConfirm(ids: Set<String>)
}