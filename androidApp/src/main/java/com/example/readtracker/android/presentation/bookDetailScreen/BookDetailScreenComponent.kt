package com.example.readtracker.android.presentation.bookDetailScreen

import com.example.readtracker.android.domain.entity.BookStatus
import kotlinx.coroutines.flow.StateFlow

interface BookDetailScreenComponent {

    val model: StateFlow<BookDetailScreenStore.State>

    fun onEditBookClick()

    fun onChangeStatusClick(newStatus: BookStatus)

    fun onUpdatePageClick(newPage: Int)
}