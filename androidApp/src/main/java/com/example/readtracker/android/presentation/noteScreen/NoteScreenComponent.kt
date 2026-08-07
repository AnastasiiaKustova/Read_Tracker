package com.example.readtracker.android.presentation.noteScreen

interface NoteScreenComponent {
    fun onAddNoteClick()
    fun onNoteClick(noteId: String)
}