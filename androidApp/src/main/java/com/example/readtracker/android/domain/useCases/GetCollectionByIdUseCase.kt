package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.bookCollection.BookCollection
import com.example.readtracker.android.domain.repository.BooksRepository
import javax.inject.Inject

class GetCollectionByIdUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    suspend operator fun invoke(id: String) : BookCollection = repository.getCollection(id)
}