package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.MainScreenItem
import com.example.readtracker.android.domain.repository.BooksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMainScreenDataUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    // Оператор invoke позволяет вызывать класс как обычную функцию: useCase()
    operator fun invoke(): Flow<MainScreenItem> {
        return repository.mainScreenFlow // Просто возвращаем ваш реактивный поток настроенный ранее
    }
}