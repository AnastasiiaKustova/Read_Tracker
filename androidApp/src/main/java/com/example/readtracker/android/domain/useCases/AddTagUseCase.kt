package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.repository.NotesRepository
import javax.inject.Inject

class AddTagUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke() = repository.addTag()
}