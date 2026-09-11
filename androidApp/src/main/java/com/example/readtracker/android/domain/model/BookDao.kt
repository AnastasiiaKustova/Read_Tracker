package com.example.readtracker.android.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.readtracker.android.domain.entity.book.BookEntity
import com.example.readtracker.android.domain.entity.book.BookWithNoteStatsData
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    // Получить все книги (автоматически обновляет поток при изменениях)
    @Query("SELECT * FROM books")
    fun getAllBooksFlow(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<BookEntity>

    // Поиск конкретной книги по ID
    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: String): BookEntity?

    @Query("SELECT * FROM books WHERE id IN (:bookIds)")
    suspend fun getBooksByIds(bookIds: Set<String>): List<BookEntity>

    // Фильтрация книг по статусу чтения
    @Query("SELECT * FROM books WHERE bookStatusString = :status")
    suspend fun getBooksByStatus(status: String): List<BookEntity>

    // Сохранить или обновить книгу (если ID совпал, перезапишет данные)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBook(bookId: String)

    @Query("""
    SELECT 
        books.*, 
        COUNT(DISTINCT notes.id) AS notesCount, 
        MIN(stats.timestamp) AS firstReadingDate, 
        MAX(stats.timestamp) AS lastReadingDate,
        MAX(CASE WHEN stats.statusChangedTo = 'FINISHED' THEN stats.timestamp END) AS finishedDate
    FROM books 
    LEFT JOIN notes ON books.id = notes.bookId 
    LEFT JOIN stats ON books.id = stats.bookId
    GROUP BY books.id
""")
    suspend fun getAllBooksWithFullStats(): List<BookWithNoteStatsData>

    @Query("""
    SELECT 
        books.*, 
        COUNT(DISTINCT notes.id) AS notesCount, 
        MIN(stats.timestamp) AS firstReadingDate, 
        MAX(stats.timestamp) AS lastReadingDate,
        MAX(CASE WHEN stats.statusChangedTo = 'FINISHED' THEN stats.timestamp END) AS finishedDate
    FROM books 
    LEFT JOIN notes ON books.id = notes.bookId 
    LEFT JOIN stats ON books.id = stats.bookId
    WHERE books.id = :bookId
    GROUP BY books.id
""")
    suspend fun getBookWithFullStatsById(bookId: String): BookWithNoteStatsData?
}
