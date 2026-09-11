package com.example.readtracker.android.presentation.searchBookScreen

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.presentation.bookListScreen.BookVerticalRow
import com.example.readtracker.android.presentation.common.CommonError

@Composable
fun SearchBookScreenContent(component: SearchBookScreenComponent) {

    val state by component.model.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Каталог книг",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 3. Компонент строки поиска
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { component.onQueryChange(it) }, // Обновляем текст при вводе
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Поиск по названию или автору...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Иконка поиска")
            },
            trailingIcon = {
                // Если пользователь что-то ввел, показываем крестик для быстрой очистки поля
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { component.onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Очистить")
                    }
                }
            },
            singleLine = true // Строка ввода должна быть строго в один ряд
        )

        if (state.isLoading) {
            Text(text = "Загрузка...", style = MaterialTheme.typography.bodyLarge)
        }

        when (val contentState = state.screenState) {
            SearchBookScreenStore.State.ScreenState.Initial -> {}
            SearchBookScreenStore.State.ScreenState.Error -> CommonError()
            is SearchBookScreenStore.State.ScreenState.Loaded -> {
                // Передаем сет книг в список результатов
                if (!state.isLoading)
                    StatsScreen(
                        searchQuery = state.searchQuery,
                        books = contentState.books.toList(),
                        onBookClick = { book -> component.onBookClick(book) },
                    )
            }
        }
    }
}

@Composable
private fun StatsScreen(
    searchQuery: String,
    books: List<BookItem>,
    onBookClick: (BookItem) -> Unit,
) {
    if (books.isEmpty()) {
        Text(
            text = "Ничего не найдено по запросу \"$searchQuery\"",
            style = MaterialTheme.typography.bodyLarge
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = books,
                key = { book -> book.id }
            ) { book ->
                BookVerticalRow(
                    book = Book(
                        id = book.id.toString(),
                        title = book.title ?: "Без названия",
                        author = book.author ?: "Автор не указан",
                        description = book.description ?: "Описание отсутствует",
                        coverUri = book.picture?.let { Uri.parse(it) },
                        totalPages = 0,
                        currentPage = 0,
                        rating = 0,
                        series = book.series ?: "",
                        idLitres = book.id,
                        bookStatus = BookStatus.READING
                    ),
                    isSelectionMode = false,
                    isSelected = false,
                    onBookClick = { onBookClick(book) },
                )
            }
        }
    }
}
