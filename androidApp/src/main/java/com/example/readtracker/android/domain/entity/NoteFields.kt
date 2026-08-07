package com.example.readtracker.android.domain.entity

interface NoteFields {
    val quoteText: String?
    val pageNumber: Int?
    val userComment: String
    val book: Book
    val tags: Set<Tag>
    val isPublic: Boolean
}