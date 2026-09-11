package com.example.readtracker.android.presentation.noteScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.readtracker.android.domain.entity.tag.Tag
import com.example.readtracker.android.presentation.common.CommonError
import com.example.readtracker.android.presentation.common.CommonInitial
import com.example.readtracker.android.presentation.common.CommonLoading
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenStore
import com.example.readtracker.android.presentation.ui.NotesListScreen
import kotlin.collections.plus

@Composable
fun NoteScreenContent(component: NoteScreenComponent) {
    val state by component.model.collectAsState()

    Box{
        when(val screenState = state.screenState){
            NoteScreenStore.State.ScreenState.Error -> CommonError()
            NoteScreenStore.State.ScreenState.Initial -> CommonInitial()
            is NoteScreenStore.State.ScreenState.Loaded -> {
                NotesListScreen(
                    noteSet = screenState.notes,
                    tagSet = screenState.tags,
                    onAddNoteClick = {component.onAddNoteClick()},
                    onNoteClick = {noteId ->
                        component.onNoteClick(noteId) },
                )
            }
            NoteScreenStore.State.ScreenState.Loading -> CommonLoading()
        }
    }
}