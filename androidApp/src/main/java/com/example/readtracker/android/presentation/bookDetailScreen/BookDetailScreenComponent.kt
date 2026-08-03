package com.example.readtracker.android.presentation.bookDetailScreen

import kotlinx.coroutines.flow.StateFlow

interface BookDetailScreenComponent {

    val model: StateFlow<BookDetailScreenStore.State>

    fun onEditBookClick()

    fun onChangeStatusClick()

    fun onUpdatePageClick()
}