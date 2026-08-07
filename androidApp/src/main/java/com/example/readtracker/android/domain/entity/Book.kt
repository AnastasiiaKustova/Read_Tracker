package com.example.readtracker.android.domain.entity

data class Book(
    val id: String,
    override val title: String,
    override val author: String,
    override val description: String,
    override val coverUri: android.net.Uri?,
    override val totalPages: Int,
    val currentPage: Int,
    val quotesCount: Int,
    val bookStatus: BookStatus,
) : BookFields {
    companion object{
        fun test() = Book(
            id = "01",
            title = "Очень длинное название",
            author = "Автор Такойто",
            description = "Очень длинное описание про книгу. Прям очень длинное описание про книгу. Настолько что занимает прям очень много строчек.",
            currentPage = 120, // Поставил реальное число, чтобы прогресс-бар не улетал в космос
            totalPages = 400,
            quotesCount = 1,
            bookStatus = BookStatus.FINISHED,
            coverUri = null
        )
    }
}