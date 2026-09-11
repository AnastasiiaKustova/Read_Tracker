package com.example.readtracker.android.presentation.addNoteScreen

import com.example.readtracker.android.domain.entity.note.AddNoteInput
import com.example.readtracker.android.domain.entity.tag.AddTagInput
import kotlinx.coroutines.flow.StateFlow


interface AddNoteScreenComponent {
    val model: StateFlow<AddNoteScreenStore.State>

    fun onSaveClick(addNoteInput: AddNoteInput)

    fun onAddTagClick(addTagInput: AddTagInput)

    fun onBookSelected(bookId: String)

    fun onBookSelectClick()
}