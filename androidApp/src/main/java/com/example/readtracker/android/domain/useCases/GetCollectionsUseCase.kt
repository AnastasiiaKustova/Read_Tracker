package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.BookCollection
import com.example.readtracker.android.domain.repository.MainScreenRepository
import javax.inject.Inject

class GetCollectionsUseCase @Inject constructor(
    private val repository: MainScreenRepository
) {
    suspend operator fun invoke() : Set<BookCollection> = repository.getCollections()
}