package com.example.readtracker.android.domain.entity.stats

import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.book.Book

data class ReadStat (
    val id: String,
    val timestamp: Long,
    override val book: Book,
    override val pagesRead: Int,
    override val durationMinutes: Int,
    override val statusChangedTo: BookStatus?
): StatsFields