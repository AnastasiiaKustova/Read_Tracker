package com.example.readtracker.android.domain.entity

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val currentPage: Int,
    val totalPages: Int,
    val quotesCount: Int,
    val bookStatus: BookStatus
){
    companion object{
        fun test() = Book(
            id = "01",
            title = "Очень длинное название",
            author = "Автор Такойто",
            description = "",
            currentPage = 120, // Поставил реальное число, чтобы прогресс-бар не улетал в космос
            totalPages = 400,
            quotesCount = 1,
            bookStatus = BookStatus.READING
        )
    }
}