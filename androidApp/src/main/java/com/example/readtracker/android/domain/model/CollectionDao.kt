package com.example.readtracker.android.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.readtracker.android.domain.entity.BookCollectionBookCrossRef
import com.example.readtracker.android.domain.entity.BookCollectionEntity
import com.example.readtracker.android.domain.entity.BookCollectionWithBooksEntity
import com.example.readtracker.android.domain.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface  CollectionDao {
    // 1. Получить поток всех заметок с их тегами (для автоматического обновления UI)
    @Transaction
    @Query("SELECT * FROM collections")
    fun getAllCollectionsFlow(): Flow<List<BookCollectionWithBooksEntity>>

    // 2. Получить список всех заметок с их тегами (разовый запрос)
    @Transaction
    @Query("SELECT * FROM collections")
    suspend fun getAllCollections(): List<BookCollectionWithBooksEntity>

    // 3. Поиск одной заметки со всеми её тегами по ID
    @Transaction
    @Query("SELECT * FROM collections WHERE id = :collectionId")
    suspend fun getCollectionById(collectionId: String): BookCollectionWithBooksEntity?

    // 4. Поиск списка заметок с их тегами по множеству ID
    @Transaction
    @Query("SELECT * FROM collections WHERE id IN (:collectionIds)")
    suspend fun getCollectionsByIds(collectionIds: Set<String>): List<BookCollectionWithBooksEntity>

    @Transaction
    @Query("""
        SELECT * FROM collections 
        WHERE id IN (
            SELECT collectionId FROM collection_book_cross_ref 
            WHERE bookId IN (:bookIds)
        )
    """)
    suspend fun getCollectionsByBookIds(bookIds: Set<String>): List<BookCollectionWithBooksEntity>

    // 5. Базовый низкоуровневый инсерт самой заметки в таблицу collections
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionEntity(collection: BookCollectionEntity)

    // 6. Базовый инсерт связи заметки и тега в промежуточную таблицу
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionBookCrossRefs(crossRefs: List<BookCollectionBookCrossRef>)

    // 7. УНИВЕРСАЛЬНЫЙ МЕТОД СОХРАНЕНИЯ (Используйте его в Репозитории!)
    // Он в рамках одной транзакции сохраняет и заметку, и пачку её тегов
    @Transaction
    suspend fun insertCollectionWithBooks(collection: BookCollectionEntity, books: List<BookEntity>) {
        // Сначала сохраняем саму заметку
        insertCollectionEntity(collection)

        // Формируем список связей Many-to-Many между ID этой заметки и всеми её тегами
        val crossRefs = books.map { book ->
            BookCollectionBookCrossRef(collectionId = collection.id, bookId = book.id)
        }

        // Записываем связи в промежуточную таблицу
        if (crossRefs.isNotEmpty()) {
            insertCollectionBookCrossRefs(crossRefs)
        }
    }

    // 8. Удаление заметки по ID
    @Query("DELETE FROM collections WHERE id = :collectionId")
    suspend fun deleteCollection(collectionId: String)
}