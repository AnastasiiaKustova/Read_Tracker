package com.example.readtracker.android.presentation.addNoteScreen

import com.example.readtracker.android.domain.entity.AddNoteInput
import kotlinx.coroutines.flow.StateFlow


interface AddNoteScreenComponent {
    val model: StateFlow<AddNoteScreenStore.State>

    fun onSaveClick(addNoteInput: AddNoteInput)
}