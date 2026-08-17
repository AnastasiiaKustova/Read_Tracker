package com.example.readtracker.android.domain.entity.book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val idLitres: Long?,
    val title: String,
    val author: String,
    val description: String,
    val series: String,
    val coverUriString: String?,
    val totalPages: Int,
    val currentPage: Int,
    val quotesCount: Int,
    val bookStatusString: String
)