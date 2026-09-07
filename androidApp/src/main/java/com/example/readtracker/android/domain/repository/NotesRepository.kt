package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.entity.note.AddNoteInput
import com.example.readtracker.android.domain.entity.tag.AddTagInput
import com.example.readtracker.android.domain.entity.note.Note
import com.example.readtracker.android.domain.entity.tag.Tag

interface NotesRepository {

    suspend fun addNote(addNoteInput: AddNoteInput)

    suspend fun getNote(id: String) : Note

    suspend fun getNotesByBookId(id: String) : Set<Note>

    suspend fun addTag(addTagInput: AddTagInput)

    suspend fun getTag(id: String) : Tag

    suspend fun getTags(): Set<Tag>

    suspend fun getNotes(): Set<Note>

}