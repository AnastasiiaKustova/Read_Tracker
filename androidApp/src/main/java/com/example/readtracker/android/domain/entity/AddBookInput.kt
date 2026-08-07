package com.example.readtracker.android.domain.entity

data class AddBookInput(
    override val title: String,
    override val author: String,
    override val description: String,
    override val totalPages: Int,
    override val coverUri: android.net.Uri?
) : BookFields

fun AddBookInput.toBook(id: String, status: BookStatus = BookStatus.READING): Book {
    return Book(
        id = id,
        title = this.title,
        author = this.author,
        description = this.description,
        totalPages = this.totalPages,
        coverUri = this.coverUri,
        currentPage = 0,
        quotesCount = 0,
        bookStatus = status
    )
}