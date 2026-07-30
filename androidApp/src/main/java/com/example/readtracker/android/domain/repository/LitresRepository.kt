package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.model.BookDao

interface LitresRepository {
    suspend fun getPopularBooks(): List<BookDao>
}