package com.example.readtracker.android.domain.entity

data class Note (
    val id: String,
    override val quoteText: String?,
    override val pageNumber: Int?,
    override val userComment: String,
    override val book: Book,
    override val tags: Set<Tag>,
    override val isPublic: Boolean,
    val createdAt: String,
) : NoteFields