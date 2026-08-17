package com.example.readtracker.android.domain.entity.note

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "note_tag_cross_ref", primaryKeys = ["noteId", "tagId"],indices = [Index(value = ["tagId"])])
data class NoteTagCrossRef(
    val noteId: String,
    val tagId: String
)