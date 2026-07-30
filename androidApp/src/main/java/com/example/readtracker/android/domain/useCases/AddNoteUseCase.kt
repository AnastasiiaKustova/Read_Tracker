package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.repository.NotesRepository
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke() = repository.addNote()
}