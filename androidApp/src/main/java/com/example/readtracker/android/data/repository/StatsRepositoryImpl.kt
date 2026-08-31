package com.example.readtracker.android.data.repository

import com.example.readtracker.android.data.mapper.toDomainList
import com.example.readtracker.android.data.mapper.toEntity
import com.example.readtracker.android.domain.entity.PeriodTab
import com.example.readtracker.android.domain.entity.StatsDetailForTrackerPeriod
import com.example.readtracker.android.domain.entity.stats.AddStatsInput
import com.example.readtracker.android.domain.entity.stats.toReadStat
import com.example.readtracker.android.domain.model.StatsDao
import com.example.readtracker.android.domain.repository.StatsRepository
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao
) : StatsRepository {
    override suspend fun addActivity(addStatsInput: AddStatsInput) {
        val generatedId = UUID.randomUUID().toString()
        // Сохраняем точный текущий таймстамп создания записи (число Long)
        val createdAt = System.currentTimeMillis()
        val finalActivity = addStatsInput.toReadStat(generatedId, createdAt)

        // Вызываем метод вставки в Room
        statsDao.insertActivity(finalActivity.toEntity())
    }

    override suspend fun getStatistics(
        date: Date,
        periodTab: PeriodTab
    ): StatsDetailForTrackerPeriod {
        val calendar = Calendar.getInstance().apply { time = date }
        val todayCal = Calendar.getInstance()

        // Выбранное число месяца для календаря (например, 18)
        val selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

        // --- 1. ВЫЧИСЛЯЕМ ОБЩИЕ ГРАНИЦЫ ДЛЯ ЗАПРОСОВ В БАЗУ ДАННЫХ ---
        val (startTimestamp, endTimestamp) = when (periodTab) {
            PeriodTab.DAYS -> {
                // Границы всей текущей недели (Понедельник 00:00:00 - Воскресенье 23:59:59)
                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val diffToMonday =
                    if (currentDayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDayOfWeek
                calendar.add(Calendar.DAY_OF_MONTH, diffToMonday)
                calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(
                    Calendar.MINUTE,
                    0
                ); calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis

                calendar.add(Calendar.DAY_OF_MONTH, 6)
                calendar.set(Calendar.HOUR_OF_DAY, 23); calendar.set(
                    Calendar.MINUTE,
                    59
                ); calendar.set(Calendar.SECOND, 59); calendar.set(Calendar.MILLISECOND, 999)
                val end = calendar.timeInMillis
                start to end
            }

            PeriodTab.WEEKS -> {
                // Показываем блок из 7 недель. Находим понедельник текущей недели
                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val diffToMonday =
                    if (currentDayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDayOfWeek
                calendar.add(Calendar.DAY_OF_MONTH, diffToMonday)
                calendar.set(Calendar.HOUR_OF_DAY, 23); calendar.set(
                    Calendar.MINUTE,
                    59
                ); calendar.set(Calendar.SECOND, 59); calendar.set(Calendar.MILLISECOND, 999)
                val end = calendar.timeInMillis // Конец — это конец текущей недели

                // Старт — отматываем назад на 6 полных недель (всего 7 недель на графике)
                calendar.add(Calendar.WEEK_OF_YEAR, -6)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(
                    Calendar.MINUTE,
                    0
                ); calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                start to end
            }

            PeriodTab.MONTHS -> {
                // Границы всего текущего года (1 января 00:00 - 31 декабря 23:59)
                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(
                    Calendar.MINUTE,
                    0
                ); calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis

                calendar.set(Calendar.MONTH, Calendar.DECEMBER)
                calendar.set(Calendar.DAY_OF_MONTH, 31)
                calendar.set(Calendar.HOUR_OF_DAY, 23); calendar.set(
                    Calendar.MINUTE,
                    59
                ); calendar.set(Calendar.SECOND, 59); calendar.set(Calendar.MILLISECOND, 999)
                val end = calendar.timeInMillis
                start to end
            }

            PeriodTab.CALENDAR -> {
                // Старт — 1-е число текущего месяца, 00:00:00
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis

                // Конец — последнее число текущего месяца, 23:59:59
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                calendar.set(Calendar.HOUR_OF_DAY, 23); calendar.set(Calendar.MINUTE, 59); calendar.set(Calendar.SECOND, 59); calendar.set(Calendar.MILLISECOND, 999)
                val end = calendar.timeInMillis
                start to end
            }
        }

        // --- 2. ВЫЧИСЛЯЕМ ИНДЕКС ВЫБРАННОГО СТОЛБИКА НА ГРАФИКЕ ---
        calendar.time = date // Сбрасываем календарь на исходную дату
        val selectedBarIndex = when (periodTab) {
            PeriodTab.DAYS -> {
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
            }

            PeriodTab.WEEKS -> 6 // Выбранная (текущая) неделя всегда будет последней на графике
            PeriodTab.MONTHS -> calendar.get(Calendar.MONTH) // Индекс месяца (0..11)
            PeriodTab.CALENDAR -> 0
        }

        // --- 3. СБОР АКТИВНЫХ ДНЕЙ ДЛЯ СЕТКИ КАЛЕНДАРЯ (ЗА ТЕКУЩИЙ МЕСЯЦ) ---
        val monthCal = Calendar.getInstance().apply { time = date }
        monthCal.set(Calendar.DAY_OF_MONTH, 1)
        monthCal.set(Calendar.HOUR_OF_DAY, 0); monthCal.set(Calendar.MINUTE, 0); monthCal.set(
            Calendar.SECOND,
            0
        ); monthCal.set(Calendar.MILLISECOND, 0)
        val monthStart = monthCal.timeInMillis

        monthCal.set(Calendar.DAY_OF_MONTH, monthCal.getActualMaximum(Calendar.DAY_OF_MONTH))
        monthCal.set(Calendar.HOUR_OF_DAY, 23); monthCal.set(Calendar.MINUTE, 59); monthCal.set(
            Calendar.SECOND,
            59
        ); monthCal.set(Calendar.MILLISECOND, 999)
        val monthEnd = monthCal.timeInMillis

        val allMonthActivities = statsDao.getActivitiesForPeriod(monthStart, monthEnd)
        val activeDays = mutableSetOf<Int>()
        val tempCal = Calendar.getInstance()
        for (activity in allMonthActivities) {
            tempCal.timeInMillis = activity.stats.timestamp
            activeDays.add(tempCal.get(Calendar.DAY_OF_MONTH))
        }

        // --- 4. ЗАПРОС ОБЩИХ СУММАРНЫХ МЕТРИК ЗА ВЕСЬ ПЕРИОД ЭКРАНА ---
        val metricsDto = statsDao.getPeriodMetrics(startTimestamp, endTimestamp)
        val totalMinutesAll = metricsDto?.totalMinutes ?: 0
        val hours = totalMinutesAll / 60
        val remainingMinutes = totalMinutesAll % 60
        val timeStr = String.format("%d:%02d:00", hours, remainingMinutes)

        // --- 5. РАСЧЕТ ИДЕНТИЧНЫХ ВЕРТИКАЛЬНЫХ СТОЛБИКОВ (БАРОВ) ГРАФИКА ---
        val chartHeights = mutableListOf<Float>()

        if (periodTab != PeriodTab.CALENDAR) {
            val barCount = if (periodTab == PeriodTab.MONTHS) 12 else 7
            val stepCal = Calendar.getInstance().apply { timeInMillis = startTimestamp }
            var maxPagesInChunk = 1
            val rawPagesPerBar = mutableListOf<Int>()

            for (i in 0 until barCount) {
                val chunkStart = stepCal.timeInMillis

                // Нарезаем время шагом, который соответствует табу
                when (periodTab) {
                    PeriodTab.DAYS -> stepCal.add(Calendar.DAY_OF_MONTH, 1) // Шаг — 1 день
                    PeriodTab.WEEKS -> stepCal.add(Calendar.WEEK_OF_YEAR, 1) // Шаг — 1 неделя
                    PeriodTab.MONTHS -> stepCal.add(Calendar.MONTH, 1)      // Шаг — 1 месяц
                    else -> {}
                }
                val chunkEnd = stepCal.timeInMillis - 1

                // Считаем сумму страниц строго внутри этого кусочка (дня, недели или месяца)
                val chunkMetrics = statsDao.getPeriodMetrics(chunkStart, chunkEnd)
                val pagesInChunk = chunkMetrics?.totalPages ?: 0
                rawPagesPerBar.add(pagesInChunk)
                if (pagesInChunk > maxPagesInChunk) {
                    maxPagesInChunk = pagesInChunk
                }
            }

            // Нормализуем высоты для UI от 0.0 до 1.0
            for (pages in rawPagesPerBar) {
                chartHeights.add(pages.toFloat() / maxPagesInChunk.toFloat())
            }
        }

        // --- 6. ЗАПРОС И ФИЛЬТРАЦИЯ СПИСКА СЕССИЙ ДЛЯ НИЖНЕГО СПИСКА ---
        // База по-прежнему возвращает пачку данных за весь период (чтобы не делать повторный SQL-запрос)
        val allPeriodEntities = statsDao.getActivitiesForPeriod(startTimestamp, endTimestamp)
        val allPeriodList = allPeriodEntities.toDomainList()

        // Фильтруем список сессий в зависимости от того, какой столбик (бар) сейчас выбран пользователем
        val filteredActivitiesList = when (periodTab) {
            PeriodTab.DAYS -> {
                // Вычисляем точный день, который сейчас выбран на графике
                val filterCal = Calendar.getInstance().apply { time = date }
                val currentDayOfWeek = filterCal.get(Calendar.DAY_OF_WEEK)
                val diffToMonday = if (currentDayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDayOfWeek
                filterCal.add(Calendar.DAY_OF_MONTH, diffToMonday) // Встали на понедельник недели
                filterCal.add(Calendar.DAY_OF_MONTH, selectedBarIndex) // Сдвинулись на выбранный день (0 = Пн, 1 = Вт...)

                val targetDay = filterCal.get(Calendar.DAY_OF_MONTH)
                val targetMonth = filterCal.get(Calendar.MONTH)
                val targetYear = filterCal.get(Calendar.YEAR)

                // Оставляем в списке только те сессии, которые были сделаны именно в этот день
                val compareCal = Calendar.getInstance()
                allPeriodList.filter { activity ->
                    compareCal.timeInMillis = activity.timestamp
                    compareCal.get(Calendar.DAY_OF_MONTH) == targetDay &&
                            compareCal.get(Calendar.MONTH) == targetMonth &&
                            compareCal.get(Calendar.YEAR) == targetYear
                }
            }

            PeriodTab.WEEKS -> {
                // Вычисляем границы выбранной из 7 недель
                val filterCal = Calendar.getInstance().apply { time = date }
                val currentDayOfWeek = filterCal.get(Calendar.DAY_OF_WEEK)
                val diffToMonday = if (currentDayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDayOfWeek
                filterCal.add(Calendar.DAY_OF_MONTH, diffToMonday) // Встали на понедельник текущей недели

                // Сдвигаемся назад относительно последней недели (индекс 6 — это текущая неделя, индекс 5 — прошлая и т.д.)
                val weeksBack = 6 - selectedBarIndex
                filterCal.add(Calendar.WEEK_OF_YEAR, -weeksBack)

                filterCal.set(Calendar.HOUR_OF_DAY, 0); filterCal.set(Calendar.MINUTE, 0); filterCal.set(Calendar.SECOND, 0); filterCal.set(Calendar.MILLISECOND, 0)
                val weekStart = filterCal.timeInMillis

                filterCal.add(Calendar.DAY_OF_MONTH, 6)
                filterCal.set(Calendar.HOUR_OF_DAY, 23); filterCal.set(Calendar.MINUTE, 59); filterCal.set(Calendar.SECOND, 59); filterCal.set(Calendar.MILLISECOND, 999)
                val weekEnd = filterCal.timeInMillis

                // Оставляем только те сессии, которые попадают в рамки этой конкретной недели
                allPeriodList.filter { activity ->
                    activity.timestamp in weekStart..weekEnd
                }
            }

            PeriodTab.MONTHS -> {
                // Фильтруем строго по выбранному месяцу года (selectedBarIndex равен 0..11)
                val compareCal = Calendar.getInstance()
                allPeriodList.filter { activity ->
                    compareCal.timeInMillis = activity.timestamp
                    compareCal.get(Calendar.MONTH) == selectedBarIndex
                }
            }

            PeriodTab.CALENDAR -> {
                // Фильтруем список сессий строго по тому числу, которое выбрано в календаре
                val compareCal = Calendar.getInstance()
                val targetCal = Calendar.getInstance().apply { time = date }

                val targetDay = targetCal.get(Calendar.DAY_OF_MONTH)
                val targetMonth = targetCal.get(Calendar.MONTH)
                val targetYear = targetCal.get(Calendar.YEAR)

                allPeriodList.filter { activity ->
                    compareCal.timeInMillis = activity.timestamp
                    compareCal.get(Calendar.DAY_OF_MONTH) == targetDay &&
                            compareCal.get(Calendar.MONTH) == targetMonth &&
                            compareCal.get(Calendar.YEAR) == targetYear
                }
            }
        }

        // --- 7. СБОРКА ИТОГОВОГО ОБЪЕКТА ---
        return StatsDetailForTrackerPeriod(
            selectedBarIndex = selectedBarIndex,
            totalTimeStr = timeStr,
            totalPagesStr = (metricsDto?.totalPages ?: 0).toString(),
            totalBooksStr = (metricsDto?.totalBooks ?: 0).toString(),
            chartHeights = chartHeights,
            activities = filteredActivitiesList, // <-- Передаем отфильтрованный список!
            selectedDay = selectedDay,
            activeDays = activeDays
        )
    }
}