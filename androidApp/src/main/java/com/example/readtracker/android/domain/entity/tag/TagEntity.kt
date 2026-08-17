package com.example.readtracker.android.domain.entity.tag

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val id: String,
    val title: String,
)