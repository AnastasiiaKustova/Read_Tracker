package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.repository.BooksRepository
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    suspend operator fun invoke(collectionId: String?, bookStatus: BookStatus?) : Set<Book> = repository.getBooks(collectionId, bookStatus)
}