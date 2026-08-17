package com.example.readtracker.android.domain.entity.book

import android.net.Uri
import com.example.readtracker.android.domain.entity.BookStatus

data class AddBookInput(
    override val title: String,
    override val author: String,
    override val description: String,
    override val totalPages: Int,
    override val coverUri: Uri?,
    override val series: String,
    override val idLitres: Long?,
) : BookFields

fun AddBookInput.toBook(id: String, status: BookStatus = BookStatus.READING): Book {
    return Book(
        id = id,
        title = this.title,
        author = this.author,
        description = this.description,
        totalPages = this.totalPages,
        coverUri = this.coverUri,
        series = this.series,
        idLitres = this.idLitres,
        currentPage = 0,
        quotesCount = 0,
        bookStatus = status
    )
}