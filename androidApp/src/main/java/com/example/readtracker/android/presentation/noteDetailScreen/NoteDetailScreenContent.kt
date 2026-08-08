package com.example.readtracker.android.presentation.noteDetailScreen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.presentation.common.CommonError
import com.example.readtracker.android.presentation.common.CommonInitial
import com.example.readtracker.android.presentation.common.CommonLoading

@Composable
fun NoteDetailScreenContent(component: NoteDetailScreenComponent) {
    val state by component.model.collectAsState()

    Box{
        when(val screenState = state.screenState){
            NoteDetailScreenStore.State.ScreenState.Error -> CommonError()
            NoteDetailScreenStore.State.ScreenState.Initial -> CommonInitial()
            is NoteDetailScreenStore.State.ScreenState.Loaded -> {
                NoteDetailScreen(
                    note = screenState.note,
                    onEditClicked = { component.onEditClick() },
                    onDeleteClicked = { component.onDeleteClick() }
                )
            }
            NoteDetailScreenStore.State.ScreenState.Loading -> CommonLoading()
        }
    }
}



@Composable
fun NoteDetailScreen(
    note: Note,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentScrollState = rememberScrollState()
    // Состояние для раскрытия цитаты по клику
    var isQuoteExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // --- 1. СТАТИЧНАЯ ШАПКА (Всегда на месте) ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = note.book.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = note.book.author,
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // СПИСОК ТЕГОВ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (note.tags.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE5E5E5))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Без тега", fontSize = 12.sp, color = Color.Gray)
                    }
                } else {
                    note.tags.toList().forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE5E5E5))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. ПРОКРУЧИВАЕМЫЙ ЦЕНТРАЛЬНЫЙ КОНТЕНТ ---
        // Скролл применяется только к этой колонке, шапка и подвал не двигаются
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Забирает всё свободное место между шапкой и подвалом
                .verticalScroll(contentScrollState)
        ) {
            // ДИНАМИЧЕСКИЙ БЛОК ЦИТАТЫ
            if (note.quoteText != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        // Вешаем клик на всю карточку для раскрытия текста
                        .clickable { isQuoteExpanded = !isQuoteExpanded },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E5E5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        if (note.pageNumber != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "стр. ${note.pageNumber}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Text(
                            text = "“${note.quoteText}”",
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF555555),
                            lineHeight = 24.sp,
                            // Если раскрыто — бесконечно строк, если скрыто — максимум 6
                            maxLines = if (isQuoteExpanded) Int.MAX_VALUE else 6,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // БЛОК МЫСЛЕЙ / КОММЕНТАРИЯ ПОЛЬЗОВАТЕЛЯ
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (note.quoteText != null) "Мои мысли" else "Заметка",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.userComment.ifEmpty { "Текст заметки пустой..." },
                    fontSize = 16.sp,
                    color = if (note.userComment.isEmpty()) Color.LightGray else Color.Black,
                    lineHeight = 24.sp
                )
            }

            // Небольшой внутренний отступ снизу прокрутки, чтобы текст не прижимался вплотную к кнопкам
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. СТАТИЧНЫЙ ПОДВАЛ (Всегда на месте в самом низу) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(), // Учитывает системную полоску навигации
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Создано",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = note.createdAt,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка редактирования (без паразитных кругов)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE5E5E5))
                        .clickable { onEditClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать заметку",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Кнопка удаления (без паразитных кругов)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFEBEE))
                        .clickable { onDeleteClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить заметку",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}