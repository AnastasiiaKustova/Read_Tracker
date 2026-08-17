package com.example.readtracker.android.domain.entity

import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.bookCollection.BookCollection


data class MainScreenItem (
    val books: Set<Book>,
    val collections: Set<BookCollection>
){
    companion object{
        fun default() = MainScreenItem(emptySet(), emptySet())
    }
}