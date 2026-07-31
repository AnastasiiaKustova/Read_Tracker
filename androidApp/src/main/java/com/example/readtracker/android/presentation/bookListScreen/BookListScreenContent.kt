package com.example.readtracker.android.presentation.bookListScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.readtracker.android.domain.entity.Book

@Composable
fun BookListScreenContent(component: BookListScreenComponent) {
    BookListScreen(
        title = "",
        books = emptyList(),
        onBackClick = { component.onBackClick() },
        onBookClick = { bookId -> component.onBookClick(bookId) },
    )
}

@Composable
fun BookListScreen(
    title: String,            // Прокидываем из компонента (имя коллекции или таба)
    books: List<Book>,        // Отфильтрованный список книг
    onBackClick: () -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Статичная шапка с сортировкой всегда на месте
        BookListHeader(
            title = title,
            bookCount = books.size,
            currentSortName = "По дате добавления",
            onBackClick = onBackClick,
            onSortClick = { /* TODO: Открыть меню сортировки */ }
        )

        // Проматываемый вниз список книг
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp) // Расстояние между карточками книг
        ) {
            items(books, key = { it.id }) { book ->
                BookVerticalRow(
                    book = book,
                    onBookClick = onBookClick
                )
            }
        }
    }
}

@Preview
@Composable
fun BookListScreenTest(){
    BookListScreen(
        title = "Моя коллекция",
        books = listOf(Book.test()),
        onBookClick = {},
        onBackClick = {}
    )
}