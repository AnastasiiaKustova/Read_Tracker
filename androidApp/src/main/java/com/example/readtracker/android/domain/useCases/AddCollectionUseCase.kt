package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.AddCollectionInput
import com.example.readtracker.android.domain.repository.BooksRepository
import javax.inject.Inject

class AddCollectionUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    suspend operator fun invoke(addCollectionInput: AddCollectionInput) = repository.addCollection(addCollectionInput)
}