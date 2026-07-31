package com.example.readtracker.android.presentation.mainScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookCollection
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.presentation.ui.BooksCarousel
import com.example.readtracker.android.presentation.ui.CollectionsSection
import com.example.readtracker.android.presentation.ui.ReadingStatusSection

@Composable
fun MainScreenContent(component: MainScreenComponent) {
    MainScreen(
        onBookClicked = {bookId ->
            Log.d("APP_DEBUG", "1. UI: Кликнули на книгу с ID = $bookId. Передаем в компонент.")
            component.onBookClick(bookId) },
    )
}

@Composable
private fun MainScreen(
    onBookClicked: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        //Spacer(modifier = Modifier.height(20.dp))

        val dummyBooks = setOf(
            Book("0","Название в две строчки или может в три и вс...", "Автор Такойто", "",12345, 15456, 0, BookStatus.READING),
            Book("1","Мастер и Маргарита", "Михаил Булгаков", "",200, 450, 0, BookStatus.READING),
            Book("2","Преступление и наказание", "Федор Достоевский", "",50, 600, 0, BookStatus.READING)
        )

        BooksCarousel(
            dummyBooks,
            onBookClick = { bookId ->
                onBookClicked(bookId)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        val dummyCollections = setOf(
            // Тестовые данные под ваш макет
            BookCollection("1", "Любимые", dummyBooks),
            BookCollection("2", "Какая-то ко...", emptySet()),
            BookCollection("3", "Прочитано", emptySet()),
            BookCollection("4", "Хочу купить", emptySet())
        )
        CollectionsSection(dummyCollections)

        Spacer(modifier = Modifier.height(12.dp))

        ReadingStatusSection(
            onStatusClick = {},
        )

    }
}