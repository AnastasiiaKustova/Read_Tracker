package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.BookStatus // Укажите ваш правильный пакет

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeStatusDialog(
    currentStatus: BookStatus,
    onDismissRequest: () -> Unit,
    onStatusSelected: (BookStatus) -> Unit,
    onBookCompleted: (rating: Int, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val availableStatuses by remember(currentStatus) {
        derivedStateOf { BookStatus.entries.filter { it != currentStatus } }
    }

    // Состояние: переключились ли мы на шаг оценки (когда выбрали "Прочитано")
    var isCompletedFlow by remember { mutableStateOf(false) }

    // Данные отзыва
    var rating by remember { mutableIntStateOf(0) }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Динамический заголовок в зависимости от шага
                Text(
                    text = if (isCompletedFlow) "Поздравляем с прочтением!" else "Сменить статус чтения",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (!isCompletedFlow) {
                    // --- ШАГ 1: ВЫБОР СТАТУСА ---
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        availableStatuses.forEach { status ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFE5E5E5))
                                    .clickable {
                                        // Предполагаю, что твой статус называется COMPLETED или типа того. Поправь под свой Enum!
                                        if (status == BookStatus.FINISHED) {
                                            isCompletedFlow = true
                                        } else {
                                            onStatusSelected(status)
                                        }
                                    },
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = status.icon,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = status.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // --- ШАГ 2: ОЦЕНКА И ЗАМЕТКА (Появляется плавно при выборе "Прочитано")
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Ваша оценка книги:",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        // Интерактивные звездочки рейтинга (от 1 до 5)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                        ) {
                            for (i in 1..5) {
                                val isSelected = i <= rating
                                Icon(
                                    imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFFFFD700) else Color.LightGray,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { rating = i }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Поле ввода заметки/отзыва
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = { Text("Напишите ваши впечатления о книге...", fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedIndicatorColor = Color.DarkGray,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- КНОПКИ УПРАВЛЕНИЯ ВНИЗУ ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "Отмена", color = Color.DarkGray, fontSize = 16.sp)
                    }

                    // Если мы на шаге отзыва, добавляем кнопку сохранения финала
                    if (isCompletedFlow) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onBookCompleted(rating, noteText) },
                            enabled = rating > 0, // Не даем сохранить без оценки
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                disabledContainerColor = Color.LightGray
                            )
                        ) {
                            Text("Готово", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}