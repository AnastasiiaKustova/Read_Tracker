package com.example.readtracker.android.data.repository

import com.example.readtracker.android.data.local.CoverStorage
import com.example.readtracker.android.data.mapper.toDomain
import com.example.readtracker.android.data.mapper.toDomainSet
import com.example.readtracker.android.data.mapper.toEntity
import com.example.readtracker.android.data.mapper.toEntityList
import com.example.readtracker.android.domain.entity.book.AddBookInput
import com.example.readtracker.android.domain.entity.bookCollection.AddCollectionInput
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.bookCollection.BookCollection
import com.example.readtracker.android.domain.entity.book.BookEntity
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.MainScreenItem
import com.example.readtracker.android.domain.entity.MainScreenItem.Companion.default
import com.example.readtracker.android.domain.entity.book.BookWithStats
import com.example.readtracker.android.domain.entity.book.toBook
import com.example.readtracker.android.domain.entity.book.toDomain
import com.example.readtracker.android.domain.entity.book.toDomainSet
import com.example.readtracker.android.domain.entity.bookCollection.toBookCollection
import com.example.readtracker.android.domain.model.BookDao
import com.example.readtracker.android.domain.model.CollectionDao
import com.example.readtracker.android.domain.repository.BooksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class BooksRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val collectionDao: CollectionDao,
    private val coverStorage: CoverStorage
) : BooksRepository {

    private val repositoryJob = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    private val _booksState = MutableStateFlow(default())
    override val mainScreenFlow: Flow<MainScreenItem> = _booksState.asStateFlow()

    override suspend fun addBook(addBookInput: AddBookInput) {
        val generatedId = java.util.UUID.randomUUID().toString()
        val sourceUri = addBookInput.coverUri
        val internalCoverUri = if (sourceUri != null && (sourceUri.scheme == "http" || sourceUri.scheme == "https")) {
            sourceUri
        } else {
            coverStorage.saveCoverToInternalStorage(sourceUri, generatedId)
        }
        val finalBook = addBookInput.toBook(generatedId).copy(coverUri = internalCoverUri)
        bookDao.insertBook(finalBook.toEntity())
    }

    override suspend fun updateBook(updatedBook: Book){
        val sourceUri = updatedBook.coverUri
        val internalCoverUri = if (sourceUri != null && (sourceUri.scheme == "http" || sourceUri.scheme == "https")) {
            sourceUri
        } else {
            coverStorage.saveCoverToInternalStorage(sourceUri, updatedBook.id)
        }
        val finalBook = updatedBook.copy(coverUri = internalCoverUri)
        bookDao.insertBook(finalBook.toEntity())
    }


    override suspend fun getBook(id: String): Book {
        val entity = bookDao.getBookById(id) ?: throw Exception("Книга не найдена")
        return entity.toDomain()
    }

    override suspend fun getBookWithStats(id: String): BookWithStats {
        val entity = bookDao.getBookWithFullStatsById(id) ?: throw Exception("Книга не найдена")
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

    override suspend fun getBooksWithFullStats(): Set<BookWithStats> {
        val entities = bookDao.getAllBooksWithFullStats()
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
            combine(
                bookDao.getAllBooksFlow().onStart { emit(emptyList()) },
                collectionDao.getAllCollectionsFlow().onStart { emit(emptyList()) }
            ) { bookEntities, collectionEntities ->
                // Перегоняем List из Room в ваши доменные Set через наши мапперы коллекций
                MainScreenItem(
                    books = bookEntities.toDomainSet(),
                    collections = collectionEntities.toDomainSet()
                )
            }.collect { updatedMainScreenItem ->
                // Как только в Room что-то меняется, репозиторий обновляет ваш _booksState!
                _booksState.value = updatedMainScreenItem
            }
        }
    }
}