package com.example.readtracker.android.domain.entity

data class Note (
    val id: String,
    val quoteText: String?,    // Сама цитата из книги
    val pageNumber: Int?,     // Номер страницы (опционально)
    val userComment: String,
    val book: Book, //bookId: String,
    val tags: Set<Tag>,
    val createdAt: String
){
    companion object{
        fun test() = Note(
            id = "0",
            quoteText = "Тут какая-то супер пупер интересная цитата. Разные описания, какие-то диалоги, еще что-то интересное, в несколько строчек. Тут какая-то супер пупер интересная цитата. Разные описания, какие-то диалоги, еще что-то интересное, в несколько строчек. Тут какая-то супер пупер интересная цитата. Разные описания, какие-то диалоги, еще что-то интересное, в несколько строчек.",
            pageNumber = 10,
            userComment = "Тут какая-то супер пупер интересное мнение читателя. Его мысли и еще что-то интересное, в несколько строчек. Тут какая-то супер пупер интересное мнение читателя. Его мысли и еще что-то интересное, в несколько строчек. Тут какая-то супер пупер интересное мнение читателя. Его мысли и еще что-то интересное, в несколько строчек.",
            book = Book.test(),
            tags = setOf(Tag.test1(), Tag.test2()),
            createdAt = "24.05.2026"
        )
        fun testShort() = Note(
            id = "1",
            quoteText = null,
            pageNumber = null,
            userComment = "Это комментарий",
            book = Book.test(),
            tags = setOf(Tag.test2()),
            createdAt = "24.05.2026"
        )
        fun testWithoutTags() = Note(
            id = "2",
            quoteText = null,
            pageNumber = null,
            userComment = "Это комментарий",
            book = Book.test(),
            tags = emptySet(),
            createdAt = "24.05.2026"
        )
    }
}