package com.example.readtracker.android.presentation.bookListScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookStatus
import kotlin.math.round

@Composable
fun BookVerticalRow(
    book: Book,
    isSelectionMode: Boolean, // Передаем, нужно ли показывать чекбокс
    isSelected: Boolean,      // Состояние чекбокса
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (book.totalPages > 0) book.currentPage.toFloat() / book.totalPages else 0f
    val percentage = round((progress * 100)).toInt()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE5E5E5))
            .clickable { onBookClick(book.id) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Обложка книги
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFB0B3B8))
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Текстовая информация (занимает всё оставшееся место)
        Column(
            modifier = Modifier
                .weight(1f)
                .height(100.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = book.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = book.author,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // Динамический блок прогресса в зависимости от статуса
            if (book.bookStatus == BookStatus.FINISHED) {
                // Если прочитано — просто пишем статус без полосы прогресса
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2F0D9)) // Мягкий зеленый фон статуса
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Прочитано",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF385723)
                    )
                }
            } else {
                // Если книга в процессе или отложена — выводим прогресс-бар
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Прогресс: $percentage%",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${book.currentPage}/${book.totalPages}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(2.5.dp)),
                        color = Color(0xFF007AFF),
                        trackColor = Color(0xFFCCCCCC)
                    )
                }
            }
        }

        // 5. ДОБАВЛЯЕМ ЧЕКБОКС: Если включен режим мультивыбора, рисуем галочку справа
        if (isSelectionMode) {
            Spacer(modifier = Modifier.width(8.dp))
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onBookClick(book.id) }, // Клик по чекбоксу делает то же, что и клик по карточке
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF007AFF), // Синяя галочка
                    uncheckedColor = Color.Gray
                )
            )
        }
    }
}