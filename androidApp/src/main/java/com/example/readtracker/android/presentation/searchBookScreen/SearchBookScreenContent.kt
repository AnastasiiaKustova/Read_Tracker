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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookItem
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.presentation.bookListScreen.BookVerticalRow
import com.example.readtracker.android.presentation.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SearchBookScreenContent(component: SearchBookScreenComponent) {
    StatsScreen(
        onBookClick = { book -> component.onBookClick(book) },
        onBackClick = { component.onBackClick() }
    )
}

@Composable
private fun StatsScreen(
    onBookClick: (BookItem) -> Unit,
    onBackClick: () -> Unit
) {
    var books by remember { mutableStateOf<List<BookItem>>(listOf()) }
    var isLoading by remember { mutableStateOf(true) }

    // 1. Переменная для хранения текста поиска
    var searchQuery by remember { mutableStateOf("") }

    // 2. LaunchedEffect теперь будет перезапускаться КАЖДЫЙ РАЗ, когда меняется searchQuery
    LaunchedEffect(searchQuery) {
        if (isLoading == false) {delay(500.milliseconds)}
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                if (searchQuery.isBlank()) {
                    // Если строка поиска пустая, просто берем первые книги каталога
//                    books = supabase.from("books")
//                        .select {
//                            range(0, 19) // Возьмем 20 книг для красивого заполнения экрана
//                        }
//                        .decodeList<BookItem>()
                    books = emptyList()
                } else {
                    // 🌟 Единый живой поиск по названию (name) и автору (author) через индекс, который мы создали
                    val rawBooks = supabase.from("books")
                        .select {
                            // Ищем, содержит ли колонка "name" (название книги) введенный текст.
                            // Символы % по бокам означают поиск в любой части строки без учета регистра.
                            filter {
                                or {
                                    // Условие 1: ищем по названию книги
                                    ilike("name", "%$searchQuery%")

                                    // Условие 2: ищем по имени автора
                                    ilike("author", "%$searchQuery%")
                                }
                            }
                            range(0, 19)
                        }
                        .decodeList<BookItem>()
                    books = rawBooks.distinctBy { it.id }.distinctBy { it.title }.sortedBy { it.title }
                }
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Каталог книг",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 3. Компонент строки поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it }, // Обновляем текст при вводе
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Поиск по названию или автору...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Иконка поиска")
            },
            trailingIcon = {
                // Если пользователь что-то ввел, показываем крестик для быстрой очистки поля
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Очистить")
                    }
                }
            },
            singleLine = true // Строка ввода должна быть строго в один ряд
        )

        if (isLoading) {
            Text(text = "Загрузка...", style = MaterialTheme.typography.bodyLarge)
        } else if (books.isEmpty()) {
            Text(text = "Ничего не найдено по запросу \"$searchQuery\"", style = MaterialTheme.typography.bodyLarge)
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
                            quotesCount = 0,
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
}