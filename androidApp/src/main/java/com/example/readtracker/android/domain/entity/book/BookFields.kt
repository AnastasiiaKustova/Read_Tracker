package com.example.readtracker.android.domain.entity.book

import android.net.Uri

interface BookFields {
    val title: String
    val author: String
    val description: String
    val totalPages: Int
    val coverUri: Uri?
    val series: String
    val idLitres: Long?
}