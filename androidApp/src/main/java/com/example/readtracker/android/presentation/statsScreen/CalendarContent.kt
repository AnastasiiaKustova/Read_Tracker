package com.example.readtracker.android.presentation.statsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.StatsDetailForTrackerPeriod
import com.example.readtracker.android.domain.entity.stats.ReadStat
import java.util.Calendar
import java.util.Date

@Composable
fun CalendarContent(
    selectedDate: Date,
    state: StatsDetailForTrackerPeriod,
    onMonthOffset: (Int) -> Unit,       // Коллбек в Стор: -1 (назад) или +1 (вперед)
    onDayClick: (Int) -> Unit           // Клик по числу дня для обновления Стора
) {
    // --- АВТОМАТИЧЕСКИЕ РАСЧЕТЫ КАЛЕНДАРЯ ВНУТРИ UI ---
    val calendar = Calendar.getInstance().apply { time = selectedDate }

    val daysInMonth =
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH) // Сколько дней в месяце (28-31)

    // Вычисляем смещение (emptySlots). В Java Calendar воскресенье = 1, понедельник = 2
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val emptySlots = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - Calendar.MONDAY

    // Определяем сегодняшний день в реальности, чтобы нарисовать обводку
    val todayCal = Calendar.getInstance()
    val isCurrentMonthAndYear = todayCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
            todayCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
    val currentDay = if (isCurrentMonthAndYear) todayCal.get(Calendar.DAY_OF_MONTH) else null

    // Логика блокировки стрелки вперед (например, нельзя листать дальше текущего реального месяца)
    val maxFutureCal = Calendar.getInstance() // Текущий реальный месяц
    val canSwipeRight = calendar.get(Calendar.YEAR) < maxFutureCal.get(Calendar.YEAR) ||
            (calendar.get(Calendar.YEAR) == maxFutureCal.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) < maxFutureCal.get(Calendar.MONTH))

    val canSwipeLeft = true // Назад в прошлое можно листать всегда

    // Форматируем красивое название месяца для шапки (например, "Май 2026")
    val monthNames = arrayOf(
        "Январь",
        "Февраль",
        "Март",
        "Апрель",
        "Май",
        "Июнь",
        "Июль",
        "Август",
        "Сентябрь",
        "Октябрь",
        "Ноябрь",
        "Декабрь"
    )
    val monthTitle = "${monthNames[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.YEAR)}"

    // --- РЕНДЕРИНГ ИНТЕРФЕЙСА ---
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Шапка с переключателями месяцев
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onMonthOffset(-1) },
                enabled = canSwipeLeft,
                modifier = Modifier.alpha(if (canSwipeLeft) 1.0f else 0.3f)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Предыдущий месяц",
                    tint = PrimaryCyan
                )
            }

            Text(
                text = monthTitle,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { onMonthOffset(1) },
                enabled = canSwipeRight,
                modifier = Modifier.alpha(if (canSwipeRight) 1.0f else 0.3f)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "Следующий месяц",
                    tint = PrimaryCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Дни недели
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("П", "В", "С", "Ч", "П", "С", "В").forEach {
                Text(
                    text = it,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Автоматическая сетка дней
        val totalSlots = emptySlots + daysInMonth
        Column {
            for (week in 0 until (totalSlots + 6) / 7) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (dayOfWeek in 0 until 7) {
                        val slotIndex = week * 7 + dayOfWeek
                        val dayNumber = slotIndex - emptySlots + 1

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayNumber in 1..daysInMonth) {
                                val isActive = state.activeDays.contains(dayNumber)
                                val isSelected = state.selectedDay == dayNumber
                                val isToday = currentDay == dayNumber

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) PrimaryCyan
                                            else if (isActive) PrimaryCyan.copy(alpha = 0.3f)
                                            else Color.Transparent
                                        )
                                        .then(
                                            if (isToday && !isSelected) Modifier.border(
                                                1.dp,
                                                PrimaryCyan,
                                                CircleShape
                                            ) else Modifier
                                        )
                                        .clickable { onDayClick(dayNumber) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        color = if (isSelected) DarkBackground else TextPrimary,
                                        fontWeight = if (isActive || isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Заголовок списка сессий (выводит правильное название месяца в родительном падеже при желании, либо просто из шапки)
        val monthInGenitive = arrayOf(
            "января",
            "февраля",
            "марта",
            "апреля",
            "мая",
            "июня",
            "июля",
            "августа",
            "сентября",
            "октября",
            "ноября",
            "декабря"
        )
        val statisticsTitle = state.selectedDay?.let {
            "Статистика за $it ${monthInGenitive[calendar.get(Calendar.MONTH)]}"
        } ?: "Выберите день"

        Text(
            text = statisticsTitle,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Нижний список прочитанного за день
        if (state.activeDays.contains(state.selectedDay)) {
            ActivityListSection(
                title = "Сессии чтения",
                activities = state.activities
            )
        } else {
            Text(
                text = "В этот день вы не зафиксировали чтение.",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}