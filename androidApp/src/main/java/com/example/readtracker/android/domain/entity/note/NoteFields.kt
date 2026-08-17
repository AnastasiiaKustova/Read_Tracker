package com.example.readtracker.android.domain.entity.note

import com.example.readtracker.android.domain.entity.tag.Tag
import com.example.readtracker.android.domain.entity.book.Book

interface NoteFields {
    val quoteText: String?
    val pageNumber: Int?
    val userComment: String
    val book: Book
    val tags: Set<Tag>
    val isPublic: Boolean
}