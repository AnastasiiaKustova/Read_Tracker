package com.example.readtracker.android.data.repository

import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.entity.Tag
import com.example.readtracker.android.domain.repository.NotesRepository
import javax.inject.Inject

class NotesRepositoryImpl@Inject constructor(): NotesRepository {
    override suspend fun addNote() {
        TODO("Not yet implemented")
    }

    override suspend fun addTag() {
        TODO("Not yet implemented")
    }

    override suspend fun getTags(): Set<Tag> {
        TODO("Not yet implemented")
    }

    override suspend fun getNotes(): Set<Note> {
        TODO("Not yet implemented")
    }
}