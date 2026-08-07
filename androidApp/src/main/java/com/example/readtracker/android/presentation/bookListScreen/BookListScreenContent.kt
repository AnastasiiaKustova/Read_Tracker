package com.example.readtracker.android.presentation.bookListScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookListMode
import com.example.readtracker.android.presentation.common.CommonError
import com.example.readtracker.android.presentation.common.CommonInitial
import com.example.readtracker.android.presentation.common.CommonLoading

@Composable
fun BookListScreenContent(component: BookListScreenComponent) {

    val state by component.model.collectAsState()

    Box{
        when(val screenState = state.screenState){
            BookListScreenStore.State.ScreenState.Error -> CommonError()
            BookListScreenStore.State.ScreenState.Initial -> CommonInitial()
            is BookListScreenStore.State.ScreenState.Loaded -> {
                BookListScreen(
                    title = screenState.title,
                    books = screenState.books.toList(),
                    onBackClick = { component.onBackClick() },
                    onBookClick = { bookId -> component.onBookClick(bookId) },
                    onMultiSelectConfirm = { ids -> component.onMultiSelectConfirm(ids)},
                    mode = screenState.mode
                )
            }
            BookListScreenStore.State.ScreenState.Loading -> CommonLoading()
        }
    }
}

@Composable
fun BookListScreen(
    title: String,
    books: List<Book>,
    mode: BookListMode, // 1. Передаем режим работы экрана
    onBackClick: () -> Unit,
    // Настраиваем лямбды под разные режимы:
    onBookClick: (String) -> Unit, // Нужен для VIEW и SINGLE_SELECT
    onMultiSelectConfirm: (Set<String>) -> Unit, // Нужен для MULTI_SELECT
    modifier: Modifier = Modifier
) {
    // 2. Храним ID выбранных книг (для режима мультивыбора)
    var selectedBookIds by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Статичная шапка с сортировкой всегда на месте
        BookListHeader(
            title = title,
            bookCount = books.size,
            mode = mode,
            currentSortName = "По дате добавления",
            onBackClick = onBackClick,
            onSortClick = { /* TODO: Открыть меню сортировки */ }
        )

        // Основной контейнер для списка и кнопки (используем Box, чтобы прижать кнопку к низу)
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {

            // Проматываемый вниз список книг
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // Если режим MULTI_SELECT, делаем паддинг снизу больше, чтобы список не уходил под кнопку
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = if (mode == BookListMode.MULTI_SELECT) 80.dp else 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(books, key = { it.id }) { book ->

                    // Проверяем, выбрана ли данная книга в режиме мультивыбора
                    val isSelected = selectedBookIds.contains(book.id)

                    // 3. Вызываем обновленный BookVerticalRow (код ниже)
                    BookVerticalRow(
                        book = book,
                        isSelectionMode = mode == BookListMode.MULTI_SELECT, // Показываем ли чекбокс
                        isSelected = isSelected, // Активен ли чекбокс
                        onBookClick = { bookId ->
                            when (mode) {
                                BookListMode.VIEW -> {
                                    // Обычный режим: открываем экран деталей
                                    onBookClick(bookId)
                                }
                                BookListMode.SINGLE_SELECT -> {
                                    // Режим единичного выбора: сразу возвращаем выбранную книгу назад
                                    onBookClick(bookId)
                                }
                                BookListMode.MULTI_SELECT -> {
                                    // Режим мультивыбора: добавляем/удаляем ID из нашего сета
                                    selectedBookIds = if (isSelected) {
                                        selectedBookIds - bookId
                                    } else {
                                        selectedBookIds + bookId
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // 4. Показываем кнопку подтверждения ТОЛЬКО в режиме MULTI_SELECT
            if (mode == BookListMode.MULTI_SELECT) {
                Button(
                    onClick = { onMultiSelectConfirm(selectedBookIds) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .height(50.dp)
                        .navigationBarsPadding(), // Учитывает системную полоску навигации
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                    enabled = selectedBookIds.isNotEmpty() // Кнопка активна, только если выбрана хотя бы 1 книга
                ) {
                    Text(
                        text = "Выбрать (${selectedBookIds.size})",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
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
        onBackClick = {},
        mode = BookListMode.MULTI_SELECT,
        onMultiSelectConfirm = {}
    )
}