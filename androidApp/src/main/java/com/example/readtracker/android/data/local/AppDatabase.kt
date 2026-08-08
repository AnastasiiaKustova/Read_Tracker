package com.example.readtracker.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.readtracker.android.domain.entity.BookCollectionBookCrossRef
import com.example.readtracker.android.domain.entity.BookCollectionEntity
import com.example.readtracker.android.domain.entity.BookEntity
import com.example.readtracker.android.domain.entity.NoteEntity
import com.example.readtracker.android.domain.entity.NoteTagCrossRef
import com.example.readtracker.android.domain.entity.TagEntity
import com.example.readtracker.android.domain.model.BookDao
import com.example.readtracker.android.domain.model.CollectionDao
import com.example.readtracker.android.domain.model.NoteDao
import com.example.readtracker.android.domain.model.TagDao

@Database(entities = [BookEntity::class, NoteEntity::class, TagEntity::class, NoteTagCrossRef::class, BookCollectionEntity::class, BookCollectionBookCrossRef::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
    abstract fun collectionDao(): CollectionDao
}
