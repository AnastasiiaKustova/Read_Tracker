package com.example.readtracker.android.domain.entity.book

import androidx.room.Embedded
import com.example.readtracker.android.data.mapper.toDomain

data class BookWithNoteStatsData(
    @Embedded val bookEntity: BookEntity,
    val notesCount: Int,
    val firstReadingDate: Long?,
    val lastReadingDate: Long?,
    val finishedDate: Long?
)

data class BookWithStats(
    val book: Book,
    val notesCount: Int,
    val firstReadingDate: Long?,
    val lastReadingDate: Long?,
    val finishedDate: Long?
)

@JvmName("bookWithNoteToBookWithStats")
fun BookWithNoteStatsData.toDomain(): BookWithStats {
    return BookWithStats(
        book = this.bookEntity.toDomain(),
        notesCount = this.notesCount,
        firstReadingDate = this.firstReadingDate,
        lastReadingDate = this.lastReadingDate,
        finishedDate = this.finishedDate
    )
}

@JvmName("bookWithNoteListToBookWithStatsSet")
fun List<BookWithNoteStatsData>.toDomainSet(): Set<BookWithStats> {
    return this.map { it.toDomain() }.toSet()
}