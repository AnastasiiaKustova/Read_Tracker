package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookCollection
import com.example.readtracker.android.domain.entity.MainScreenItem
import kotlinx.coroutines.flow.Flow

interface MainScreenRepository {
    val mainScreenFlow: Flow<MainScreenItem>

    suspend fun addBook()

    suspend fun getBooks(): List<Book>

    suspend fun addCollection()

    suspend fun getCollections(): List<BookCollection>
}