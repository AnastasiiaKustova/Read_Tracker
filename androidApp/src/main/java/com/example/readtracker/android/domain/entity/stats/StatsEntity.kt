package com.example.readtracker.android.domain.entity.stats

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.readtracker.android.domain.entity.book.BookEntity

@Entity(
    tableName = "stats",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.NO_ACTION, // <-- ИЗМЕНИТЕ НА ЭТО
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["bookId"])] // Добавляем индекс для скорости)
)
data class StatsEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val bookId: String,
    val pagesRead: Int,
    val durationMinutes: Int,
    val statusChangedTo: String?
)
