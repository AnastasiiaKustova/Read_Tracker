package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.repository.LitresRepository
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val repository: LitresRepository
) {
    suspend operator fun invoke(query: String): Set<BookItem> {
        return repository.searchBooks(query)
    }
}