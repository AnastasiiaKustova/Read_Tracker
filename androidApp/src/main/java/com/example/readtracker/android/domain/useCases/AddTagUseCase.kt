package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.tag.AddTagInput
import com.example.readtracker.android.domain.repository.NotesRepository
import javax.inject.Inject

class AddTagUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(addTagInput: AddTagInput) = repository.addTag(addTagInput)
}