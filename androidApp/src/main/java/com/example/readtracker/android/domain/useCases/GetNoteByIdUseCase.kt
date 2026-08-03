package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.repository.NotesRepository
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(id: String): Note = repository.getNote(id)
}