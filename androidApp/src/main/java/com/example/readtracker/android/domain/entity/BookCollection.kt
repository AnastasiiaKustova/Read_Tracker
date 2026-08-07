package com.example.readtracker.android.domain.entity

data class BookCollection(
    val id: String,
    override val title: String,
    override val bookIds: Set<String>
): BookCollectionFields{
    companion object{
        fun test() = BookCollection(
            id = "0",
            title = "Тестовая коллекция",
            bookIds = emptySet()
        )
    }
}
