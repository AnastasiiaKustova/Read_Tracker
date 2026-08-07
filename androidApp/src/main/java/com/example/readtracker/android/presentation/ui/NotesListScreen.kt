package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.entity.Tag

@Composable
fun NotesListScreen(
    noteSet: Set<Note>,
    tagSet: Set<Tag>,
    onAddNoteClick: () -> Unit,
    onNoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTagId by remember { mutableStateOf<String?>(null) }

    // Явно приводим результат внутри derivedStateOf к List<Note>
    val filteredNotes: List<Note> by remember(searchQuery, selectedTagId, noteSet) {
        derivedStateOf {
            noteSet.filter { note ->
                // 1. Фильтр по выбранному тегу
                val matchesTag = if (selectedTagId == null) {
                    true
                } else {
                    note.tags.any { tag -> tag.id == selectedTagId }
                }

                // 2. Фильтр по поисковому запросу
                val matchesSearch = note.userComment.contains(searchQuery, ignoreCase = true) ||
                        //note.quoteText?.contains(searchQuery, ignoreCase = true) ||
                        note.book.author.contains(searchQuery, ignoreCase = true) ||
                        note.book.title.contains(searchQuery, ignoreCase = true)

                matchesTag && matchesSearch
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Шапка экрана
        item {
            NotesHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                tagSet = tagSet,
                selectedTagId = selectedTagId,
                onTagSelect = { selectedTagId = it },
                onAddNoteClick = { onAddNoteClick() }
            )
        }

        // Теперь items без проблем примет отфильтрованный List<Note>
        items(
            items = filteredNotes,
            key = { it.id } // Убедитесь, что у вашего класса Note есть поле id типа String или Int
        ) { note ->
            NoteCard(
                note = note,
                onCardClick = { onNoteClick(note.id) })
        }
    }
}