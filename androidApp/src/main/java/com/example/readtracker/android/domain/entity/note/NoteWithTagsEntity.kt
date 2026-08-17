package com.example.readtracker.android.domain.entity.note

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.readtracker.android.domain.entity.tag.TagEntity
import com.example.readtracker.android.domain.entity.book.BookEntity


data class NoteWithTagsEntity(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "id"
    )
    val book: BookEntity,
    @Relation(
        parentColumn = "id",                 // ID из таблицы notes
        entityColumn = "id",                 // ID из таблицы tags
        associateBy = Junction(
            value = NoteTagCrossRef::class,  // Наша промежуточная таблица связей
            parentColumn = "noteId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
