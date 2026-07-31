package com.example.readtracker.android.presentation.bookDetailScreen

import com.example.readtracker.android.domain.entity.Book

interface BookDetailScreenComponent {

    fun onEditBookClick()

    fun onChangeStatusClick()

    fun onUpdatePageClick()
}