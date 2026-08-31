package com.example.readtracker.android.domain.entity.stats

import androidx.room.Embedded
import androidx.room.Relation
import com.example.readtracker.android.domain.entity.book.BookEntity

data class StatsWithBookEntity(
    @Embedded
    val stats: StatsEntity,

    @Relation(
        parentColumn = "bookId", // Поле из таблицы stats
        entityColumn = "id"      // Поле из таблицы книг (BookEntity)
    )
    val book: BookEntity
)