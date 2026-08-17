package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.entity.database.CategoryItem

interface LitresRepository {
    suspend fun searchBooks(query: String): Set<BookItem>
    suspend fun searchCategories(ids: String): List<CategoryItem>
}