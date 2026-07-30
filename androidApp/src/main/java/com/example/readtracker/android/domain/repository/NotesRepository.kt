package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.entity.Tag

interface NotesRepository {

    suspend fun addNote()

    suspend fun addTag()

    suspend fun getTags(): Set<Tag>

    suspend fun getNotes(): Set<Note>

}