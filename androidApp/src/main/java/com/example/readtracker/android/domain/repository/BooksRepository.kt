package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.MainScreenItem
import com.example.readtracker.android.domain.entity.book.AddBookInput
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.book.BookWithStats
import com.example.readtracker.android.domain.entity.bookCollection.AddCollectionInput
import com.example.readtracker.android.domain.entity.bookCollection.BookCollection
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    val mainScreenFlow: Flow<MainScreenItem>

    suspend fun addBook(addBookInput: AddBookInput)

    suspend fun updateBook(updatedBook: Book)

    suspend fun getBook(id: String): Book

    suspend fun getBookWithStats(id: String): BookWithStats

    suspend fun getBooks(collectionId: String?, bookStatus: BookStatus?): Set<Book>

    suspend fun getBooksWithFullStats(): Set<BookWithStats>

    suspend fun addCollection(addCollectionInput: AddCollectionInput)

    suspend fun getCollection(id: String): BookCollection

    suspend fun getCollections(): Set<BookCollection>
}