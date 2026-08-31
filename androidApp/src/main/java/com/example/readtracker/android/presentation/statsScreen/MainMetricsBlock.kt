package com.example.readtracker.android.presentation.statsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.PeriodTab
import java.util.Calendar
import java.util.Date

@Composable
fun MainMetricsBlock(
    timeStr: String,
    pages: String,
    books: String,
    selectedDate: Date,     // Передаем дату из Стора
    selectedTab: PeriodTab  // Передаем текущую вкладку
) {
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    val monthNames = arrayOf(
        "янв", "фев", "мар", "апр", "май", "июн",
        "июл", "авг", "сен", "окт", "ноя", "дек"
    )
    val fullMonthNames = arrayOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )

    // --- АВТОМАТИЧЕСКИЙ РАСЧЕТ ДИАПАЗОНА ДАТ ---
    val dateRangeText = when (selectedTab) {
        PeriodTab.DAYS -> {
            // Находим Понедельник текущей недели
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val diffToMonday = if (currentDayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDayOfWeek
            calendar.add(Calendar.DAY_OF_MONTH, diffToMonday)

            val startDay = calendar.get(Calendar.DAY_OF_MONTH)
            val startMonth = monthNames[calendar.get(Calendar.MONTH)]
            val startYear = calendar.get(Calendar.YEAR)

            // Перемещаемся на Воскресенье этой же недели
            calendar.add(Calendar.DAY_OF_MONTH, 6)
            val endDay = calendar.get(Calendar.DAY_OF_MONTH)
            val endMonth = monthNames[calendar.get(Calendar.MONTH)]
            val endYear = calendar.get(Calendar.YEAR)

            if (startYear == endYear) {
                if (startMonth == endMonth) "$startDay – $endDay $startMonth $startYear г."
                else "$startDay $startMonth – $endDay $endMonth $startYear г."
            } else {
                "$startDay $startMonth $startYear г. – $endDay $endMonth $endYear г."
            }
        }

        PeriodTab.WEEKS -> {
            // Находим Воскресенье текущей недели (конец диапазона)
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val diffToMonday = if (currentDayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDayOfWeek
            calendar.add(Calendar.DAY_OF_MONTH, diffToMonday + 6)

            val endDay = calendar.get(Calendar.DAY_OF_MONTH)
            val endMonth = monthNames[calendar.get(Calendar.MONTH)]
            val endYear = calendar.get(Calendar.YEAR)

            // Отматываем назад ровно на 7 недель (48 дней назад от Воскресенья), чтобы найти Понедельник первой недели
            calendar.add(Calendar.DAY_OF_MONTH, -48)
            val startDay = calendar.get(Calendar.DAY_OF_MONTH)
            val startMonth = monthNames[calendar.get(Calendar.MONTH)]
            val startYear = calendar.get(Calendar.YEAR)

            if (startYear == endYear) {
                "$startDay $startMonth – $endDay $endMonth $startYear г."
            } else {
                "$startDay $startMonth $startYear г. – $endDay $endMonth $endYear г."
            }
        }

        PeriodTab.MONTHS -> {
            val year = calendar.get(Calendar.YEAR)
            "1 янв $year г. – 31 дек $year г."
        }

        PeriodTab.CALENDAR -> {
            // Для календаря выводим просто "Август 2026 г." в качестве заголовка месяца
            val monthStr = fullMonthNames[calendar.get(Calendar.MONTH)].replaceFirstChar { it.uppercase() }
            val year = calendar.get(Calendar.YEAR)
            "$monthStr $year г."
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Выводим красивую строку подзаголовка дат
        Text(
            text = dateRangeText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary.copy(alpha = 0.8f) // Слегка приглушенный стильный цвет
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = timeStr,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Страниц ", color = TextSecondary, fontSize = 14.sp)
            Text(text = pages, color = AccentPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "  |  Книг ", color = TextSecondary, fontSize = 14.sp)
            Text(text = " $books", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}