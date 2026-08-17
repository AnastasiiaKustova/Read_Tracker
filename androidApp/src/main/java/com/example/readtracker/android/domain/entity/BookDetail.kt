package com.example.readtracker.android.domain.entity

import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.entity.database.CategoryItem

data class BookDetail(
    val book: Book?,
    val bookItem: BookItem?,
    val categories: List<CategoryItem>,
    val mode: BookDetailMode
)