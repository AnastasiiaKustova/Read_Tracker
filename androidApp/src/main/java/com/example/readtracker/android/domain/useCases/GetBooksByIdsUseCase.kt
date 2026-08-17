package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.repository.BooksRepository
import javax.inject.Inject

class GetBooksByIdsUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    suspend operator fun invoke(ids: Set<String>): Set<Book> {
        var result : MutableSet<Book> = mutableSetOf()
        for (id in ids){
            val book = repository.getBook(id)
            result.add(book)
        }
        return result
    }
}