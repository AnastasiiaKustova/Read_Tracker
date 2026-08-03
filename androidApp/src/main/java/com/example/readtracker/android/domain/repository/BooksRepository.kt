package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookCollection
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.MainScreenItem
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    val mainScreenFlow: Flow<MainScreenItem>

    suspend fun addBook()

    suspend fun getBook(id: String): Book

    suspend fun getBooks(collectionId: String?, bookStatus: BookStatus?): Set<Book>

    suspend fun addCollection()

    suspend fun getCollection(id: String): BookCollection

    suspend fun getCollections(): Set<BookCollection>
}