package com.example.readtracker.android.presentation.addBookScreen

import com.example.readtracker.android.domain.entity.book.AddBookInput
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.database.BookItem
import kotlinx.coroutines.flow.StateFlow

interface AddBookScreenComponent {
    val model: StateFlow<AddBookScreenStore.State>
    fun onSearchLitresClick()
    fun onSaveBookClick(addBookInput: AddBookInput)
    fun onUpdateBookClick(book: Book)
    fun onSelectBookFromBaseClicked(selectedBook: BookItem)
}