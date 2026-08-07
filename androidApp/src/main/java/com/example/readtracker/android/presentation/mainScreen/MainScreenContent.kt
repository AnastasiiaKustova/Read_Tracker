package com.example.readtracker.android.presentation.mainScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookCollection
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.presentation.common.CommonError
import com.example.readtracker.android.presentation.common.CommonInitial
import com.example.readtracker.android.presentation.common.CommonLoading
import com.example.readtracker.android.presentation.ui.BooksCarousel
import com.example.readtracker.android.presentation.ui.CollectionsSection
import com.example.readtracker.android.presentation.ui.ReadingStatusSection

@Composable
fun MainScreenContent(component: MainScreenComponent) {
    val state by component.model.collectAsState()

    Box{
        when(val screenState = state.screenState){
            MainScreenStore.State.ScreenState.Error -> CommonError()
            MainScreenStore.State.ScreenState.Initial -> CommonInitial()
            is MainScreenStore.State.ScreenState.Loaded -> {
                MainScreen(
                    bookSet = screenState.books,
                    collectionSet = screenState.collections,
                    onAddBookClicked = {
                        component.onAddBookClick()
                    },
                    onAddCollectionClicked = {
                        component.onAddCollectionClick()
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
            MainScreenStore.State.ScreenState.Loading -> CommonLoading()
        }
    }
}

@Composable
private fun MainScreen(
    bookSet: Set<Book>,
    collectionSet: Set<BookCollection>,
    onAddBookClicked: () -> Unit,
    onAddCollectionClicked: () -> Unit,
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

        BooksCarousel(
            bookSet,
            onBookClick = { bookId ->
                onBookClicked(bookId)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CollectionsSection(
            collectionSet = collectionSet,
            onCollectionClick = { collectionId ->
                onCollectionClicked(collectionId)
            },
            onAddCollectionClick = { onAddCollectionClicked() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ReadingStatusSection(
            onStatusClick = { bookStatus ->
                onBookStatusClicked(bookStatus)
            },
        )
    }
}