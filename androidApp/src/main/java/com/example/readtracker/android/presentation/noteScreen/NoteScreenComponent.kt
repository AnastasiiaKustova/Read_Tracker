package com.example.readtracker.android.presentation.noteScreen

import kotlinx.coroutines.flow.StateFlow

interface NoteScreenComponent {
    val model: StateFlow<NoteScreenStore.State>
    fun onAddNoteClick()
    fun onNoteClick(noteId: String)
}