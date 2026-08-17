package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.repository.BooksRepository
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    suspend operator fun invoke(id: String): Book = repository.getBook(id)
}