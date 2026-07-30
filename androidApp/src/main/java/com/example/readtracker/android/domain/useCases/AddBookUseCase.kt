package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.repository.MainScreenRepository
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
 private val repository: MainScreenRepository
) {
    suspend operator fun invoke() = repository.addBook()
}