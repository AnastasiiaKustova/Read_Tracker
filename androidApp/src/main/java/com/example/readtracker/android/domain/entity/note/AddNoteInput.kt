package com.example.readtracker.android.domain.entity.note

import com.example.readtracker.android.domain.entity.tag.Tag
import com.example.readtracker.android.domain.entity.book.Book

data class AddNoteInput(
    override val quoteText: String?,
    override val pageNumber: Int?,
    override val userComment: String,
    override val book: Book,
    override val tags: Set<Tag>,
    override val isPublic: Boolean
) : NoteFields

fun AddNoteInput.toNote(id: String, createdAt: Long): Note {
    return Note(
        id = id,
        quoteText = this. quoteText,
        pageNumber = this.pageNumber,
        userComment = this.userComment,
        book = this.book,
        tags = this.tags,
        isPublic = this.isPublic,
        createdAt = createdAt
    )
}