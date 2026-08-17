package com.example.readtracker.android.domain.entity.note

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.readtracker.android.domain.entity.book.BookEntity

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE // Каскадное удаление
        )
    ],
    indices = [Index(value = ["bookId"])] // Добавляем индекс для скорости)
)
data class NoteEntity(
    @PrimaryKey val id: String,
    val quoteText: String?,
    val pageNumber: Int?,
    val userComment: String,
    val bookId: String,
    val isPublic: Boolean,
    val createdAt: String,
)