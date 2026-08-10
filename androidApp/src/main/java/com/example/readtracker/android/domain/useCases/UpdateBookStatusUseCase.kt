package com.example.readtracker.android.domain.useCases


import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.repository.BooksRepository
import javax.inject.Inject

class UpdateBookStatusUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    suspend operator fun invoke(updatedBook: Book) = repository.updateBook(updatedBook)
}