package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.Note
import androidx.compose.runtime.key

@Composable
fun NoteCard(
    note: Note,
    modifier: Modifier = Modifier
) {
    // 1. Внешний Box дает "воздух" сверху для выступающих тегов
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .padding(horizontal = 16.dp)
    ) {
        // 2. Основная серая карточка заметки
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE5E5E5) // Серый фон
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                // Отступ сверху внутри карточки, чтобы текст не упирался в теги
                Spacer(modifier = Modifier.height(6.dp))

                // Текст цитаты / заметки
                Text(
                    text = note.text,
                    fontSize = 16.sp,
                    color = Color.Black,
                    lineHeight = 22.sp,
                    maxLines = 4, // Задаем ограничение строк с троеточием
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Подпись: Автор · Название книги
                Text(
                    text = "${note.book.author} · ${note.book.title}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 3. Блок тегов, накладывающийся сверху на границу карточки
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp)
                .offset(y = (-10).dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (note.tags.isEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Без тега",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }
            } else {
                note.tags.toList().forEach { tag ->
                    // Обертываем в функцию key для оптимизации Compose
                    key(tag.id) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5F5F5))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tag.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}