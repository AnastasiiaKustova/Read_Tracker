package com.example.readtracker.android.presentation.statsScreen

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.readtracker.android.domain.entity.PeriodTab
import com.example.readtracker.android.domain.entity.StatsDetailForTrackerPeriod
import java.util.Calendar
import java.util.Date

@Composable
fun TrackerPeriodContent(
    selectedDate: Date,
    state: StatsDetailForTrackerPeriod,
    selectedTab: PeriodTab,
    onBarClick: (Int) -> Unit,          // Клик на столбик (передаем индекс)
    onPeriodSwipe: (Int) -> Unit,       // Сигнал в Стор: +1 (в прошлое), -1 (в будущее к текущему)
    modifier: Modifier = Modifier
) {
    // --- 1. АВТОМАТИЧЕСКИЙ РАСЧЕТ ПОДПИСЕЙ ОСЕЙ И ЗАГЛОВКА ---
    val calendar = Calendar.getInstance().apply { time = selectedDate }

    val chartLabels = when (selectedTab) {
        PeriodTab.DAYS -> {
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val diffToMonday = if (currentDayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDayOfWeek
            calendar.add(Calendar.DAY_OF_MONTH, diffToMonday)

            List(7) {
                val dayNum = calendar.get(Calendar.DAY_OF_MONTH).toString()
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                dayNum
            }
        }
        PeriodTab.WEEKS -> {
            val tempCal = Calendar.getInstance().apply { time = selectedDate }
            tempCal.add(Calendar.WEEK_OF_YEAR, -6)
            List(7) {
                val start = tempCal.get(Calendar.DAY_OF_MONTH)
                tempCal.add(Calendar.DAY_OF_MONTH, 6)
                val end = tempCal.get(Calendar.DAY_OF_MONTH)
                tempCal.add(Calendar.DAY_OF_MONTH, 1)
                "$start/$end"
            }
        }
        PeriodTab.MONTHS -> {
            listOf("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек")
        }
        else -> { return }
    }

    calendar.time = selectedDate
    val listSectionTitle = when (selectedTab) {
        PeriodTab.DAYS -> {
            val dayNames = arrayOf("Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб")
            val monthNames = arrayOf("января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря")
            "${dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]}, ${calendar.get(Calendar.DAY_OF_MONTH)} ${monthNames[calendar.get(Calendar.MONTH)]}"
        }
        PeriodTab.WEEKS -> "Активность за неделю"
        PeriodTab.MONTHS -> "Активность за месяц"
        else -> { return }
    }

    // --- 2. НАДЕЖНЫЙ РАСЧЕТ ЗАПРЕТА НА БУДУЩЕЕ ПО МИЛЛИСЕКУНДАМ ---
    val todayCal = Calendar.getInstance()
    val currentCal = Calendar.getInstance().apply { time = selectedDate }

    // Очищаем время (часы, минуты), чтобы сравнивать только календарные периоды
    todayCal.set(Calendar.HOUR_OF_DAY, 0); todayCal.set(Calendar.MINUTE, 0); todayCal.set(Calendar.SECOND, 0); todayCal.set(Calendar.MILLISECOND, 0)
    currentCal.set(Calendar.HOUR_OF_DAY, 0); currentCal.set(Calendar.MINUTE, 0); currentCal.set(Calendar.SECOND, 0); currentCal.set(Calendar.MILLISECOND, 0)

    val isAlreadyInFutureOrPresent = when (selectedTab) {
        PeriodTab.DAYS -> {
            // Если выбранный день равен сегодняшнему или больше него — мы в будущем/настоящем
            currentCal.timeInMillis >= todayCal.timeInMillis
        }
        PeriodTab.WEEKS -> {
            // Приводим обе даты к понедельникам их недель, чтобы честно сравнить недели
            todayCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            currentCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            currentCal.timeInMillis >= todayCal.timeInMillis
        }
        PeriodTab.MONTHS -> {
            // Приводим к 1-му числу месяца, чтобы сравнить месяцы
            todayCal.set(Calendar.DAY_OF_MONTH, 1)
            currentCal.set(Calendar.DAY_OF_MONTH, 1)
            currentCal.timeInMillis >= todayCal.timeInMillis
        }
        else -> false
    }

    // --- 3. ВЕРСТКА ЭКРАНА С ИСПРАВЛЕННЫМИ НАПРАВЛЕНИЯМИ ПАЛЬЦА ---
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            MainMetricsBlock(
                timeStr = state.totalTimeStr,
                pages = state.totalPagesStr,
                books = state.totalBooksStr,
                selectedDate = selectedDate,
                selectedTab = selectedTab
            )
        }

        item {
            var offsetX by remember { mutableStateOf(0f) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(selectedDate, selectedTab) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                // ПАЛЕЦ ДВИГАЛСЯ СЛЕВА НАПРАВО (Хотим открыть ПРОШЛОЕ)
                                if (offsetX > 50f) {
                                    onPeriodSwipe(-1) // Отправляем -1, чтобы Стор вычел время через календарь
                                }
                                // ПАЛЕЦ ДВИГАЛСЯ СПРАВА НАЛЕВО (Возвращаемся в БУДУЩЕЕ)
                                else if (offsetX < -50f) {
                                    if (!isAlreadyInFutureOrPresent) {
                                        onPeriodSwipe(1) // Отправляем 1, чтобы Стор прибавил время вперед
                                    }
                                }
                                offsetX = 0f // Обнуляем сдвиг
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount
                            }
                        )
                    }
            ) {
                AutoscaledBarChart(
                    heights = state.chartHeights,
                    labels = chartLabels,
                    selectedIndex = state.selectedBarIndex,
                    onBarClick = onBarClick
                )
            }
        }

        item {
            ActivityListSection(
                title = listSectionTitle,
                activities = state.activities
            )
        }
    }
}