package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.database.CategoryItem
import com.example.readtracker.android.domain.repository.LitresRepository
import javax.inject.Inject

class SearchCategoriesUseCase @Inject constructor(
    private val repository: LitresRepository
) {
    suspend operator fun invoke(ids: String): List<CategoryItem> {
        return repository.searchCategories(ids)
    }
}