package com.example.readtracker.android.domain.entity

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val currentPage: Int,
    val totalPages: Int,
    val bookStatus: BookStatus
)