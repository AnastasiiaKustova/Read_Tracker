package com.example.readtracker.android.domain.entity.bookCollection

import com.example.readtracker.android.domain.entity.book.Book

interface BookCollectionFields {
    val title: String
    val books: Set<Book>
}