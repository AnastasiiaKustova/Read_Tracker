package com.example.readtracker.android.domain.entity.bookCollection

import com.example.readtracker.android.domain.entity.book.Book

data class BookCollection(
    val id: String,
    override val title: String,
    override val books: Set<Book>
): BookCollectionFields
