package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.entity.AddNoteInput
import com.example.readtracker.android.domain.entity.AddTagInput
import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.entity.Tag

interface NotesRepository {

    suspend fun addNote(addNoteInput: AddNoteInput)

    suspend fun getNote(id: String) : Note

    suspend fun addTag(addTagInput: AddTagInput)

    suspend fun getTag(id: String) : Tag

    suspend fun getTags(): Set<Tag>

    suspend fun getNotes(): Set<Note>

}