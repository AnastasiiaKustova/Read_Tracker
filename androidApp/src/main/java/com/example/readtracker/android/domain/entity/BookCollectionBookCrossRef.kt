package com.example.readtracker.android.domain.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "collection_book_cross_ref", primaryKeys = ["collectionId", "bookId"], indices = [Index(value = ["bookId"])])
data class BookCollectionBookCrossRef (
    val collectionId: String,
    val bookId: String
)