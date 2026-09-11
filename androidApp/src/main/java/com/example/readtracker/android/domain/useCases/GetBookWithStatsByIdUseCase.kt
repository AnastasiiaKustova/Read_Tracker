package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.book.BookWithStats
import com.example.readtracker.android.domain.repository.BooksRepository
import javax.inject.Inject

class GetBookWithStatsByIdUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    suspend operator fun invoke(id: String): BookWithStats = repository.getBookWithStats(id)
}