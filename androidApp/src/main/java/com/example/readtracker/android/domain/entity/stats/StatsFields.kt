package com.example.readtracker.android.domain.entity.stats

import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.book.Book

interface StatsFields {
    val book: Book
    val pagesRead: Int
    val durationMinutes: Int
    val statusChangedTo: BookStatus?
}