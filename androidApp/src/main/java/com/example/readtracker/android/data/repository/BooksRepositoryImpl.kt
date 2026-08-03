package com.example.readtracker.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookCollection
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.MainScreenItem
import com.example.readtracker.android.domain.entity.MainScreenItem.Companion.default
import com.example.readtracker.android.domain.repository.BooksRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BooksRepositoryImpl @Inject constructor(
) : BooksRepository {

    private val repositoryJob = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    private val _booksState = MutableStateFlow(default())
    override val mainScreenFlow: Flow<MainScreenItem> = _booksState.asStateFlow()

    override suspend fun addBook() {
        TODO("Not yet implemented")
    }

    override suspend fun getBook(id: String): Book {
        return Book.test()
    }

    override suspend fun getBooks(collectionId: String?, bookStatus: BookStatus?): Set<Book> {
        return BookCollection.test().books
    }

    override suspend fun addCollection() {
        TODO("Not yet implemented")
    }

    override suspend fun getCollection(id: String): BookCollection {
        return BookCollection.test()
    }

    override suspend fun getCollections(): Set<BookCollection> {
        TODO("Not yet implemented")
    }

    init {
        repositoryScope.launch {

        }
    }
}