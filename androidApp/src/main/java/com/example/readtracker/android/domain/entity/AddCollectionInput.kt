package com.example.readtracker.android.domain.entity

data class AddCollectionInput(
    override val title: String,
    override val books: Set<Book>
): BookCollectionFields

fun AddCollectionInput.toBookCollection(id: String): BookCollection {
    return BookCollection(
        id = id,
        title = this.title,
        books = this.books
    )
}

