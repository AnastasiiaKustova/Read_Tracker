package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.tag.Tag
import com.example.readtracker.android.domain.repository.NotesRepository
import javax.inject.Inject

class GetTagsUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(): Set<Tag> = repository.getTags()
}