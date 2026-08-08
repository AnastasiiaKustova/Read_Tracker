package com.example.readtracker.android.domain.entity

interface BookCollectionFields {
    val title: String
    val books: Set<Book>
}