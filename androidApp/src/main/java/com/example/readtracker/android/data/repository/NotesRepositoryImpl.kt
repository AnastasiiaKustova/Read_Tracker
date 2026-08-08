package com.example.readtracker.android.data.repository

import com.example.readtracker.android.data.mapper.toDomain
import com.example.readtracker.android.data.mapper.toDomainSet
import com.example.readtracker.android.data.mapper.toEntity
import com.example.readtracker.android.data.mapper.toEntityList
import com.example.readtracker.android.domain.entity.AddNoteInput
import com.example.readtracker.android.domain.entity.AddTagInput
import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.entity.Tag
import com.example.readtracker.android.domain.entity.toNote
import com.example.readtracker.android.domain.model.NoteDao
import com.example.readtracker.android.domain.model.TagDao
import com.example.readtracker.android.domain.repository.NotesRepository
import javax.inject.Inject

class NotesRepositoryImpl@Inject constructor(
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
): NotesRepository {
    override suspend fun addNote(addNoteInput: AddNoteInput) {
        val generatedId = java.util.UUID.randomUUID().toString()
        val createdAt = ""
        val finalNote = addNoteInput.toNote(generatedId, createdAt)

        noteDao.insertNoteWithTags(
            note = finalNote.toEntity(),
            tags = finalNote.tags.toEntityList()
        )
    }

    override suspend fun getNote(id: String): Note {
        val entity = noteDao.getNoteById(id) ?: throw Exception("Заметка не найдена")
        return entity.toDomain()
    }

    override suspend fun addTag(addTagInput: AddTagInput) {
        val generatedId = java.util.UUID.randomUUID().toString()
        val finalTag = Tag(generatedId, addTagInput.title)
        tagDao.insertTag(finalTag.toEntity())
    }

    override suspend fun getTag(id: String): Tag {
        val entity = tagDao.getTagById(id) ?: throw Exception("Тэг не найден")
        return entity.toDomain()
    }

    override suspend fun getTags(): Set<Tag> {
        return tagDao.getAllTags().toDomainSet()
    }

    override suspend fun getNotes(): Set<Note> {
        return noteDao.getAllNotes().toDomainSet()
    }
}