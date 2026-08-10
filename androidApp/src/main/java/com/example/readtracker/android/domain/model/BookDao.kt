package com.example.readtracker.android.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.readtracker.android.domain.entity.BookEntity
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
}
