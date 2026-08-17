package com.example.readtracker.android.domain.entity.book

import android.net.Uri
import android.os.Parcelable
import com.example.readtracker.android.domain.entity.BookStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: String,
    override val title: String,
    override val author: String,
    override val description: String,
    override val coverUri: Uri?,
    override val totalPages: Int,
    override val series: String,
    override val idLitres: Long?,
    val currentPage: Int,
    val quotesCount: Int,
    val bookStatus: BookStatus,
) : BookFields, Parcelable