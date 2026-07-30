package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.repository.MainScreenRepository
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val repository: MainScreenRepository
) {
    suspend operator fun invoke() : List<Book> = repository.getBooks()
}