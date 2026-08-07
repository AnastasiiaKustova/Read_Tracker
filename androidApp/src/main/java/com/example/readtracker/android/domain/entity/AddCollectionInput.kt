package com.example.readtracker.android.domain.entity

data class AddCollectionInput(
    override val title: String,
    override val bookIds: Set<String>
): BookCollectionFields

fun AddCollectionInput.toBookCollection(id: String): BookCollection {
    return BookCollection(
        id = id,
        title = this.title,
        bookIds = this.bookIds
    )
}

