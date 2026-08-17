package com.example.readtracker.android.domain.entity.bookCollection

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.readtracker.android.domain.entity.book.BookEntity

data class BookCollectionWithBooksEntity(
    @Embedded val collection: BookCollectionEntity,
    @Relation(
        parentColumn = "id",                 // ID из таблицы collections
        entityColumn = "id",                 // ID из таблицы books
        associateBy = Junction(
            value = BookCollectionBookCrossRef::class,  // Наша промежуточная таблица связей
            parentColumn = "collectionId",
            entityColumn = "bookId"
        )
    )
    val books: List<BookEntity>
)
