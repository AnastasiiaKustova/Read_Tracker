package com.example.readtracker.android.domain.entity

data class BookCollection(
    val id: String,
    val title: String,
    val books: Set<Book>
)
