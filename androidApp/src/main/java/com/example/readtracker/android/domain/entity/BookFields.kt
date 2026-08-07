package com.example.readtracker.android.domain.entity

interface BookFields {
    val title: String
    val author: String
    val description: String
    val totalPages: Int
    val coverUri: android.net.Uri?
}