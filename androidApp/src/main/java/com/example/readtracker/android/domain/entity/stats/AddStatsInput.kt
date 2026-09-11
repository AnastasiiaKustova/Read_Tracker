package com.example.readtracker.android.domain.entity.stats

import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.book.Book

data class AddStatsInput(
    override val book: Book,
    override val pagesRead: Int,
    override val durationMinutes: Int,
    override val statusChangedTo: BookStatus?
): StatsFields

fun AddStatsInput.toReadStat(id: String, timestamp: Long): ReadStat{
    return ReadStat(
        id = id,
        timestamp = timestamp,
        book = this.book,
        pagesRead = this.pagesRead,
        durationMinutes = this.durationMinutes,
        statusChangedTo = this.statusChangedTo,
    )
}