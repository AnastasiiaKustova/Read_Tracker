package com.example.readtracker.android.presentation.noteDetailScreen

import kotlinx.coroutines.flow.StateFlow

interface NoteDetailScreenComponent {

    val model: StateFlow<NoteDetailScreenStore.State>

    fun onEditClick()

    fun onDeleteClick()

}