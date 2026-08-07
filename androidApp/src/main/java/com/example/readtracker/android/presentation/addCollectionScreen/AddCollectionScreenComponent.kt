package com.example.readtracker.android.presentation.addCollectionScreen

import com.example.readtracker.android.domain.entity.AddCollectionInput
import kotlinx.coroutines.flow.StateFlow

interface AddCollectionScreenComponent {
    val model: StateFlow<AddCollectionScreenStore.State>
    fun onSaveClick(addCollectionInput: AddCollectionInput)
    fun onAddBooks()
    fun onRemoveBookClick(id: String)
    fun onBooksSelected(ids: Set<String>)
}