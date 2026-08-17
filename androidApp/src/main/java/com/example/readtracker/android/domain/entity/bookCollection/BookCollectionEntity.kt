package com.example.readtracker.android.domain.entity.bookCollection

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class BookCollectionEntity(
    @PrimaryKey val id: String,
    val title: String,
)