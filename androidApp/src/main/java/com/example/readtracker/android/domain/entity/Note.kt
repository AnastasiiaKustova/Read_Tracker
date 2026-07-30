package com.example.readtracker.android.domain.entity

data class Note (
    val id: String,
    val text: String,
    val book: Book,
    val tags: Set<Tag>
)