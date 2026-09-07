package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.note.Note
import com.example.readtracker.android.domain.repository.NotesRepository
import javax.inject.Inject

class GetNotesByBookIdUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(id: String): Set<Note> = repository.getNotesByBookId(id)
}