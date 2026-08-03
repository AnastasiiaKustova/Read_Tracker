package com.example.readtracker.android.presentation.noteScreen

import androidx.compose.runtime.Composable
import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.entity.Tag
import com.example.readtracker.android.presentation.ui.NotesListScreen

@Composable
fun NoteScreenContent(component: NoteScreenComponent) {
    NoteScreen(
        onNoteClicked = {noteId ->
            component.onNoteClick(noteId) },
    )
}

@Composable
private fun NoteScreen(
    onNoteClicked: (String) -> Unit,
){
    // Тестовый список заметок под ваш макет
    val dummyNotes = setOf(
        Note.test(),
        Note.testShort(),
        Note.testShort(),
        Note.testWithoutTags()
    )

    val dummyTags = setOf(Tag.test1(), Tag.test2())
    NotesListScreen(
        noteSet = dummyNotes,
        tagSet = dummyTags,
        onNoteClick = { noteId ->
            onNoteClicked(noteId)
        }
    )
}