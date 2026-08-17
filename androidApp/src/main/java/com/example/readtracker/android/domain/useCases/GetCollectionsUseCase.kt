package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.bookCollection.BookCollection
import com.example.readtracker.android.domain.repository.BooksRepository
import javax.inject.Inject

class GetCollectionsUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    suspend operator fun invoke() : Set<BookCollection> = repository.getCollections()
}