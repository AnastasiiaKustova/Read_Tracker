package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.NoteScreenItem
import com.example.readtracker.android.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNoteScreenDataUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    // Оператор invoke позволяет вызывать класс как обычную функцию: useCase()
    operator fun invoke(): Flow<NoteScreenItem> {
        return repository.noteScreenFlow // Просто возвращаем ваш реактивный поток настроенный ранее
    }
}