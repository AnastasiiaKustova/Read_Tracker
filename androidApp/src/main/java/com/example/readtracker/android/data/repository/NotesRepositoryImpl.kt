package com.example.readtracker.android.data.repository

import com.example.readtracker.android.data.mapper.toDomain
import com.example.readtracker.android.data.mapper.toDomainSet
import com.example.readtracker.android.data.mapper.toEntity
import com.example.readtracker.android.data.mapper.toEntityList
import com.example.readtracker.android.domain.entity.NoteScreenItem
import com.example.readtracker.android.domain.entity.NoteScreenItem.Companion.default
import com.example.readtracker.android.domain.entity.note.AddNoteInput
import com.example.readtracker.android.domain.entity.tag.AddTagInput
import com.example.readtracker.android.domain.entity.note.Note
import com.example.readtracker.android.domain.entity.tag.Tag
import com.example.readtracker.android.domain.entity.note.toNote
import com.example.readtracker.android.domain.model.NoteDao
import com.example.readtracker.android.domain.model.TagDao
import com.example.readtracker.android.domain.repository.NotesRepository
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

class NotesRepositoryImpl@Inject constructor(
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
): NotesRepository {

    private val repositoryJob = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    private val _notesState = MutableStateFlow(default())
    override val noteScreenFlow: Flow<NoteScreenItem> = _notesState.asStateFlow()

    override suspend fun addNote(addNoteInput: AddNoteInput) {
        val generatedId = java.util.UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()
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

    override suspend fun getNotesByBookId(id: String): Set<Note> {
        return noteDao.getNoteByBookId(id).toDomainSet()
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
        return tagDao.getTagsSortedByUsage().toDomainSet()
    }

    override suspend fun getNotes(): Set<Note> {
        return noteDao.getAllNotes().toDomainSet()
    }

    init {
        repositoryScope.launch {
            combine(
                noteDao.getAllNotesFlow().onStart { emit(emptyList()) },
                tagDao.getAllTagsFlow().onStart { emit(emptyList()) }
            ) { noteEntities, tagEntities ->
                NoteScreenItem(
                    notes = noteEntities.toDomainSet(),
                    tags = tagEntities.toDomainSet()
                )
            }.collect { updatedNoteScreenItem ->
                _notesState.value = updatedNoteScreenItem
            }
        }
    }
}