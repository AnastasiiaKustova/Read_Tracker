package com.example.readtracker.android.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverUriString: String?,
    val totalPages: Int,
    val currentPage: Int,
    val quotesCount: Int,
    val bookStatusString: String
)