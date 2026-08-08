package com.example.readtracker.android.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.readtracker.android.domain.entity.NoteEntity
import com.example.readtracker.android.domain.entity.NoteTagCrossRef
import com.example.readtracker.android.domain.entity.NoteWithTagsEntity
import com.example.readtracker.android.domain.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // 1. Получить поток всех заметок с их тегами (для автоматического обновления UI)
    @Transaction
    @Query("SELECT * FROM notes")
    fun getAllNotesFlow(): Flow<List<NoteWithTagsEntity>>

    // 2. Получить список всех заметок с их тегами (разовый запрос)
    @Transaction
    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<NoteWithTagsEntity>

    // 3. Поиск одной заметки со всеми её тегами по ID
    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): NoteWithTagsEntity?

    // 4. Поиск списка заметок с их тегами по множеству ID
    @Transaction
    @Query("SELECT * FROM notes WHERE id IN (:noteIds)")
    suspend fun getNotesByIds(noteIds: Set<String>): List<NoteWithTagsEntity>

    @Transaction
    @Query("""
        SELECT * FROM notes 
        WHERE id IN (
            SELECT noteId FROM note_tag_cross_ref 
            WHERE tagId IN (:tagIds)
        )
    """)
    suspend fun getNotesByTagIds(tagIds: Set<String>): List<NoteWithTagsEntity>

    // 5. Базовый низкоуровневый инсерт самой заметки в таблицу notes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteEntity(note: NoteEntity)

    // 6. Базовый инсерт связи заметки и тега в промежуточную таблицу
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteTagCrossRefs(crossRefs: List<NoteTagCrossRef>)

    // 7. УНИВЕРСАЛЬНЫЙ МЕТОД СОХРАНЕНИЯ (Используйте его в Репозитории!)
    // Он в рамках одной транзакции сохраняет и заметку, и пачку её тегов
    @Transaction
    suspend fun insertNoteWithTags(note: NoteEntity, tags: List<TagEntity>) {
        // Сначала сохраняем саму заметку
        insertNoteEntity(note)

        // Формируем список связей Many-to-Many между ID этой заметки и всеми её тегами
        val crossRefs = tags.map { tag ->
            NoteTagCrossRef(noteId = note.id, tagId = tag.id)
        }

        // Записываем связи в промежуточную таблицу
        if (crossRefs.isNotEmpty()) {
            insertNoteTagCrossRefs(crossRefs)
        }
    }

    // 8. Удаление заметки по ID
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: String)
}