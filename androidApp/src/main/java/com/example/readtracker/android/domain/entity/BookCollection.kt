package com.example.readtracker.android.domain.entity

data class BookCollection(
    val id: String,
    val title: String,
    val books: Set<Book>
){
    companion object{
        fun test() = BookCollection(
            id = "0",
            title = "Тестовая коллекция",
            books = setOf(
                Book.test(),
                Book.test(),
                Book.test(),
                Book.test(),
                Book.test(),
            )
        )
    }
}
