package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    currentStatus: BookStatus,         // Текущий статус книги (приходит на вход)
    onDismissRequest: () -> Unit,      // Клик мимо окна или кнопка закрытия
    onStatusSelected: (BookStatus) -> Unit, // Клик по новому статусу
    modifier: Modifier = Modifier
) {
    // Через remember и derivedStateOf отфильтровываем список:
    // убираем текущий статус, чтобы показать только доступные варианты
    val availableStatuses by remember(currentStatus) {
        derivedStateOf {
            BookStatus.entries.filter { it != currentStatus }
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp), // Фирменное скругление
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // 1. Заголовок диалога
                Text(
                    text = "Сменить статус чтения",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Вертикальный список доступных статусов
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp) // Отступы между плашками
                ) {
                    availableStatuses.forEach { status ->
                        // Каждая кнопка статуса — это аккуратный серый Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE5E5E5)) // Фирменный серый фон
                                .clickable {
                                    onStatusSelected(status) // Передаем кликнутый статус наверх
                                },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Иконка статуса из вашего Enum
                                Icon(
                                    imageVector = status.icon,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                // Текст статуса из вашего Enum
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

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Единственная кнопка "Отмена" внизу справа
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            text = "Отмена",
                            color = Color.DarkGray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}