package com.example.readtracker.android.presentation.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
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
        onCloseAppClick = { component.onCloseAppClick() },
    )
}

@Composable
private fun MainScreen(
    onCloseAppClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        //Spacer(modifier = Modifier.height(20.dp))

        val dummyBooks = listOf(
            Book("0","Название в две строчки или может в три и вс...", "Автор Такойто", "",12345, 15456, BookStatus.READING),
            Book("1","Мастер и Маргарита", "Михаил Булгаков", "",200, 450, BookStatus.READING),
            Book("2","Преступление и наказание", "Федор Достоевский", "",50, 600, BookStatus.READING)
        )

        BooksCarousel(dummyBooks)

        Spacer(modifier = Modifier.height(20.dp))

        val dummyCollections = listOf(
            // Тестовые данные под ваш макет
            BookCollection("1", "Любимые", dummyBooks),
            BookCollection("2", "Какая-то ко...", emptyList()),
            BookCollection("3", "Прочитано", emptyList()),
            BookCollection("4", "Хочу купить", emptyList())
        )
        CollectionsSection(dummyCollections)

        Spacer(modifier = Modifier.height(20.dp))

        ReadingStatusSection(
            onStatusClick = {},
        )

    }
}