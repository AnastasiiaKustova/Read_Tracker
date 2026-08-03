package com.example.readtracker.android.presentation.bookDetailScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.core.formatWithSpace
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.presentation.common.CommonError
import com.example.readtracker.android.presentation.common.CommonInitial
import com.example.readtracker.android.presentation.common.CommonLoading
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenStore
import com.example.readtracker.android.presentation.ui.ChangeStatusDialog
import com.example.readtracker.android.presentation.ui.StatItem
import com.example.readtracker.android.presentation.ui.UpdatePageDialog
import org.chromium.base.Log
import kotlin.math.round

@Composable
fun BookDetailScreenContent(component: BookDetailScreenComponent) {

    val state by component.model.collectAsState()

    Box{
        when(val screenState = state.screenState){
            BookDetailScreenStore.State.ScreenState.Error -> CommonError()
            BookDetailScreenStore.State.ScreenState.Initial -> CommonInitial()
            is BookDetailScreenStore.State.ScreenState.Loaded -> {
                BookDetailScreen(
                    book = screenState.book,
                    onEditBookClick = { component.onEditBookClick() },
                    onUpdatePageClick = { component.onUpdatePageClick() },
                    onChangeStatusClick = { component.onChangeStatusClick() }
                )
            }
            BookDetailScreenStore.State.ScreenState.Loading -> CommonLoading()
        }
    }
}

@Composable
fun BookDetailScreen(
    book: Book,
    onEditBookClick: () -> Unit,       // Лямбда для карандашика редактирования
    onChangeStatusClick: () -> Unit,   // Лямбда для кнопки смены статуса
    onUpdatePageClick: () -> Unit,     // Лямбда для кнопки ввода страницы
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val progress = if (book.totalPages > 0) book.currentPage.toFloat() / book.totalPages else 0f
    val percentage = round((progress * 100)).toInt()

    var showUpdatePageDialog by remember { mutableStateOf(false) }
    var showChangeStatusDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- ВЫЗОВ ДИАЛОГА СМЕНЫ СТАТУСА ---
        if (showChangeStatusDialog) {
            ChangeStatusDialog(
                currentStatus = book.bookStatus, // Передаем текущий статус книги (например, BookStatus.READING)
                onDismissRequest = { showChangeStatusDialog = false },
                onStatusSelected = { newStatus ->
                    showChangeStatusDialog = false
                    onChangeStatusClick()
                    // TODO: Отправить Интент во МВИ стор для сохранения нового статуса в базу данных
                    android.util.Log.d("APP_DEBUG", "Выбран новый статус книги: $newStatus")
                }
            )
        }

        // --- ВЫЗОВ ДИАЛОГА ---
        if (showUpdatePageDialog) {
            UpdatePageDialog(
                totalPages = book.totalPages,
                currentPage = book.currentPage,
                onDismissRequest = { showUpdatePageDialog = false }, // Закрываем при отмене
                onConfirm = { newPage ->
                    showUpdatePageDialog = false
                    onUpdatePageClick()
                    // TODO: Отправить Интент во МВИ стор для сохранения новой страницы в базу данных
                    Log.d("APP_DEBUG", "Пользователь ввел корректную страницу: $newPage")
                }
            )
        }
    }

    // Внешний Box нужен, чтобы мы могли наложить кнопку редактирования в правый верхний угол
    Box(modifier = modifier.fillMaxSize().background(Color.White)) {

        // Основной скролл-контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Обложка книги по центру
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFB0B3B8))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Название книги
            Text(
                text = book.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Автор книги
            Text(
                text = book.author,
                fontSize = 16.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Панель статистики (Страницы и Цитаты)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(value = book.totalPages.formatWithSpace(), label = "Страниц")
                StatItem(value = book.quotesCount.toString(), label = "Заметок")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Блок прогресса чтения
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Прогресс чтения",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "$percentage%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007AFF)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF007AFF),
                    trackColor = Color(0xFFE5E5E5)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Прочитано ${book.currentPage.formatWithSpace()} из ${book.totalPages.formatWithSpace()} страниц",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- БЛОК: Кнопки трекинга (Статус и Страница) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Кнопка Смены Статуса
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE5E5E5))
                        .clickable { showChangeStatusDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Статус", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    }
                }

                // Кнопка Указать Текущую Страницу
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE5E5E5))
                        .clickable { showUpdatePageDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Я на странице", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Описание книги
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "О книге",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = book.description,
                    fontSize = 15.sp,
                    color = Color.Black,
                    lineHeight = 22.sp
                )
            }

            // ИСПРАВЛЕНИЕ: Вместо 32.dp ставим большой отступ,
            // который гарантирует, что текст вытолкнется выше системных кнопок и скрытого меню
            Spacer(
                modifier = Modifier
                    .height(80.dp) // Достаточная высота, чтобы перекрыть габариты таб-бара
                    .navigationBarsPadding() // Дополнительно учитывает системную полоску жестов Android
            )
        }

        // --- КНОПКА РЕДАКТИРОВАНИЯ (Карандашик в верхнем правом углу) ---
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 24.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { onEditBookClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Редактировать описание книги",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
@Preview
fun BookDetailScreenTest(){
    BookDetailScreen(
        Book.test(),
        {},
        {},
        {})
}