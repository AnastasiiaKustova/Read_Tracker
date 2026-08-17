package com.example.readtracker.android.presentation.bookDetailScreen

import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.entity.BookStatus
import kotlinx.coroutines.flow.StateFlow

interface BookDetailScreenComponent {

    val model: StateFlow<BookDetailScreenStore.State>

    fun onEditBookClick(book: Book)

    fun onChangeStatusClick(newStatus: BookStatus)

    fun onUpdatePageClick(newPage: Int)

    fun onChooseClick(bookItem: BookItem)
}