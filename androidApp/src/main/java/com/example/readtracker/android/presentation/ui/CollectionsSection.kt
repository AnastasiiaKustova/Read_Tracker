package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.BookCollection

@Composable
fun CollectionsSection(collectionSet: Set<BookCollection>, modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxWidth()) {
        // Шапка секции: Заголовок и кнопка «+»
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Коллекции",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            IconButton(onClick = { /* TODO: Создать новую коллекцию */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить коллекцию",
                    tint = Color.White
                )
            }
        }

        // Горизонтальный список карточек
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp), // Отступы по краям экрана
            horizontalArrangement = Arrangement.spacedBy(8.dp)
           // horizontalArrangement = Arrangement.dpToPx(12.dp).let { Arrangement.spacedBy(12.dp) } // Расстояние между карточками
        ) {
            items(collectionSet.toList()) { collection ->
                CollectionCard(
                    title = collection.title,
                    bookCount = collection.books.size
                )
            }
        }
    }
}

@Preview
@Composable
fun CollectionsSectionTest(){
    val dummyCollections = setOf(
        // Тестовые данные под ваш макет
        BookCollection("1", "Любимые", emptySet()),
        BookCollection("2", "Какая-то ко...", emptySet()),
        BookCollection("3", "Прочитано", emptySet()),
        BookCollection("4", "Хочу купить", emptySet())
    )
    CollectionsSection(dummyCollections)
}
