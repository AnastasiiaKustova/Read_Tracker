package com.example.readtracker.android.data.repository

import com.example.readtracker.android.domain.entity.AddNoteInput
import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.entity.Tag
import com.example.readtracker.android.domain.repository.NotesRepository
import javax.inject.Inject

class NotesRepositoryImpl@Inject constructor(): NotesRepository {
    override suspend fun addNote(addNoteInput: AddNoteInput) {

    }

    override suspend fun getNote(id: String): Note {
        return Note.test()
    }

    override suspend fun addTag() {

    }

    override suspend fun getTag(id: String): Tag {
        return Tag.test1()
    }

    override suspend fun getTags(): Set<Tag> {
        return setOf(Tag.test1(), Tag.test2())
    }

    override suspend fun getNotes(): Set<Note> {
        return setOf(
            Note.test(),
            Note.testShort(),
            Note.testWithoutTags(),
        )
    }
}