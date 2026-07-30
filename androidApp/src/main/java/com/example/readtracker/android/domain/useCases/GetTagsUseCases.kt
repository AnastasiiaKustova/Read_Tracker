package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.Tag
import com.example.readtracker.android.domain.repository.NotesRepository
import javax.inject.Inject

class GetTagsUseCases @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(): Set<Tag> = repository.getTags()
}