package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.ReadingDay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReadingStreakBoard(
    currentStreak: Int,
    maxStreak: Int,
    weekDays: List<ReadingDay>,
    currentDateTimestamp: Long, // <--- Теперь принимаем чистый Long таймстамп
    modifier: Modifier = Modifier
) {
    // Автоматически форматируем название месяца на основе переданной даты
    val formattedMonth = remember(currentDateTimestamp) {
        val formatter = SimpleDateFormat(
            "LLLL",
            Locale.getDefault()
        ) // "LLLL" выводит месяц в именительном падеже (например, "май", "сентябрь")
        formatter.format(Date(currentDateTimestamp))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFDFBF7))
            .padding(20.dp)
    ) {
        // --- ЗАГОЛОВОК И СТАТИСТИКА ---
        Text(
            text = "Период чтения",
            fontSize = 24.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Текущая серия: ",
                fontSize = 12.sp,
                color = Color.DarkGray
            )
            Text(
                text = "$currentStreak дня",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "  •  Самый длинный период: ",
                fontSize = 12.sp,
                color = Color.DarkGray
            )
            Text(
                text = "$maxStreak дней",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- ШАПКА КАЛЕНДАРЯ ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Дни чтения",
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = formattedMonth, // <--- Локализованный месяц подставится сам
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- СЕТКА ДНЕЙ ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.take(7).forEach { day ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = day.dayNumber.toString(),
                        fontSize = 14.sp,
                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                        color = if (day.isToday) Color(0xFFE28A5C) else Color.DarkGray
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (day.isToday) Color(0xFFFCEFE6) else Color.Transparent)
                            .border(
                                width = 1.5.dp,
                                color = when {
                                    day.isRead -> Color(0xFFE28A5C)
                                    day.isToday -> Color(0xFFE28A5C)
                                    else -> Color(0xFFE5E5E5)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day.isRead) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Прочитано",
                                tint = Color(0xFFE28A5C),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun Test(){
    val mockWeek = listOf(
        ReadingDay(dayNumber = 15, isRead = true, isToday = false),
        ReadingDay(dayNumber = 16, isRead = true, isToday = false),
        ReadingDay(dayNumber = 17, isRead = true, isToday = false),
        ReadingDay(dayNumber = 18, isRead = true, isToday = false),
        ReadingDay(dayNumber = 19, isRead = false, isToday = false), // Пропущенный
        ReadingDay(dayNumber = 20, isRead = true, isToday = true),   // Сегодня читал
        ReadingDay(dayNumber = 21, isRead = false, isToday = false)  // Будущий день
    )

    ReadingStreakBoard(
        currentStreak = 4,
        maxStreak = 16,
        weekDays = mockWeek,
        currentDateTimestamp = System.currentTimeMillis() // Передаем Long
    )
}