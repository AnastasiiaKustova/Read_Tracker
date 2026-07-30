package com.example.readtracker.android.presentation.noteScreen

import androidx.compose.runtime.Composable
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.entity.Tag
import com.example.readtracker.android.presentation.ui.NotesListScreen

@Composable
fun NoteScreenContent(component: NoteScreenComponent) {
    NoteScreen()
}

@Composable
private fun NoteScreen(){
    // Тестовый список заметок под ваш макет
    val dummyNotes = setOf(
        Note(
            id = "1",
            text = "Тут какая-то супер пупер интересная цитата. Разные описания, какие-то диалоги, еще что-то интересное, в несколько строчек...",
            book = Book.test(),
            tags = setOf(Tag.test1(), Tag.test2())
        ),
        Note(
            id = "2",
            text = "А тут маленькая цитата",
            book = Book.test(),
            tags = setOf(Tag.test2())
        ),
        Note(
            id = "3",
            text = "А тут маленькая цитата",
            book = Book.test(),
            tags = setOf(Tag.test2())
        )
        ,
        Note(
            id = "4",
            text = "А тут маленькая цитата",
            book = Book.test(),
            tags = emptySet()
        )
    )

    val dummyTags = setOf(Tag.test1(), Tag.test2())
    NotesListScreen(dummyNotes, dummyTags)
}