package com.example.readtracker.android.presentation.mainScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookCollection
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.presentation.ui.BooksCarousel
import com.example.readtracker.android.presentation.ui.CollectionsSection
import com.example.readtracker.android.presentation.ui.ReadingStatusSection

@Composable
fun MainScreenContent(component: MainScreenComponent) {
    MainScreen(
        onAddBookClicked = {
            component.onAddBookClick()
        },
        onBookClicked = {bookId ->
            Log.d("APP_DEBUG", "1. UI: Кликнули на книгу с ID = $bookId. Передаем в компонент.")
            component.onBookClick(bookId) },
        onCollectionClicked = {collectionId ->
            component.onCollectionClick(collectionId) },
        onBookStatusClicked = {bookStatus ->
            component.onCollectionClick(bookStatus) },
    )
}

@Composable
private fun MainScreen(
    onAddBookClicked: () -> Unit,
    onBookClicked: (String) -> Unit,
    onCollectionClicked: (String) -> Unit,
    onBookStatusClicked: (BookStatus) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        //Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { onAddBookClicked() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить книгу",
                    tint = Color.White
                )
            }
        }

        val dummyBooks = setOf(
            Book("0","Название в две строчки или может в три и вс...", "Автор Такойто", "",null,12345, 15456, 0, BookStatus.READING),
            Book("1","Мастер и Маргарита", "Михаил Булгаков", "",null,200, 450, 0, BookStatus.READING),
            Book("2","Преступление и наказание", "Федор Достоевский", "",null,50, 600, 0, BookStatus.READING)
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
        CollectionsSection(
            collectionSet = dummyCollections,
            onCollectionClick = { collectionId ->
                onCollectionClicked(collectionId)
            })

        Spacer(modifier = Modifier.height(12.dp))

        ReadingStatusSection(
            onStatusClick = { bookStatus ->
                onBookStatusClicked(bookStatus)
            },
        )

    }
}

@Preview
@Composable
private fun MainScreenTest(){
    MainScreen({},{},{},{})
}