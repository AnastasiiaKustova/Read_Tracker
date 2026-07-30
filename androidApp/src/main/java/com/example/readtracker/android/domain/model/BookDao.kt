package com.example.readtracker.android.domain.model

data class BookDao(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val description: String
)