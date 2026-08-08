package com.example.readtracker.android.data.repository

import com.example.readtracker.android.data.mapper.toDomain
import com.example.readtracker.android.data.mapper.toDomainSet
import com.example.readtracker.android.data.mapper.toEntity
import com.example.readtracker.android.data.mapper.toEntityList
import com.example.readtracker.android.domain.entity.AddBookInput
import com.example.readtracker.android.domain.entity.AddCollectionInput
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookCollection
import com.example.readtracker.android.domain.entity.BookEntity
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.MainScreenItem
import com.example.readtracker.android.domain.entity.MainScreenItem.Companion.default
import com.example.readtracker.android.domain.entity.toBook
import com.example.readtracker.android.domain.entity.toBookCollection
import com.example.readtracker.android.domain.model.BookDao
import com.example.readtracker.android.domain.model.CollectionDao
import com.example.readtracker.android.domain.repository.BooksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BooksRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val collectionDao: CollectionDao,
) : BooksRepository {

    private val repositoryJob = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    private val _booksState = MutableStateFlow(default())
    override val mainScreenFlow: Flow<MainScreenItem> = _booksState.asStateFlow()

    override suspend fun addBook(addBookInput: AddBookInput) {
        val generatedId = java.util.UUID.randomUUID().toString()
        val finalBook = addBookInput.toBook(generatedId)
        bookDao.insertBook(finalBook.toEntity())
    }

    override suspend fun getBook(id: String): Book {
        val entity = bookDao.getBookById(id) ?: throw Exception("Книга не найдена")
        return entity.toDomain()
    }

    override suspend fun getBooks(collectionId: String?, bookStatus: BookStatus?): Set<Book> {
        val entities: List<BookEntity> = if (collectionId != null) {
            val entity = collectionDao.getCollectionById(collectionId) ?: throw Exception("Коллекция не найдена")
            entity.books
        }
        else if (bookStatus != null) {
            bookDao.getBooksByStatus(bookStatus.name)
        }
        else {
            bookDao.getAllBooks()
        }

        return entities.toDomainSet()
    }

    override suspend fun addCollection(addCollectionInput: AddCollectionInput) {
        val generatedId = java.util.UUID.randomUUID().toString()
        val finalCollection = addCollectionInput.toBookCollection(generatedId)

        collectionDao.insertCollectionWithBooks(
            collection = finalCollection.toEntity(),
            books = finalCollection.books.toEntityList()
        )
    }

    override suspend fun getCollection(id: String): BookCollection {
        val entity = collectionDao.getCollectionById(id) ?: throw Exception("Коллекция не найдена")
        return entity.toDomain()
    }

    override suspend fun getCollections(): Set<BookCollection> {
        val entities = collectionDao.getAllCollections()

        return entities.toDomainSet()
    }

    init {
        repositoryScope.launch {

        }
    }
}