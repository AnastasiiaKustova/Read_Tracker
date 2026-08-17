package com.example.readtracker.android.presentation.searchBookScreen

import com.example.readtracker.android.domain.entity.database.BookItem
import kotlinx.coroutines.flow.StateFlow

interface SearchBookScreenComponent {
    val model: StateFlow<SearchBookScreenStore.State>

    fun onBackClick()

    fun onBookClick(bookItem: BookItem)

    fun onQueryChange(query: String)
}