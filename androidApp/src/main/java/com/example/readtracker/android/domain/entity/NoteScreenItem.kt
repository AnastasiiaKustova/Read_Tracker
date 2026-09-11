package com.example.readtracker.android.domain.entity

import com.example.readtracker.android.domain.entity.note.Note
import com.example.readtracker.android.domain.entity.tag.Tag

data class NoteScreenItem(
    val notes: Set<Note>,
    val tags: Set<Tag>
){
    companion object{
        fun default() = NoteScreenItem(emptySet(), emptySet())
    }
}
