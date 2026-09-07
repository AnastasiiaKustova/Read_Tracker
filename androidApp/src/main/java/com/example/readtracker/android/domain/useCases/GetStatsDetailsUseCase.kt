package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.core.DateTimeManager
import com.example.readtracker.android.data.mapper.toDomain
import com.example.readtracker.android.domain.entity.PeriodTab
import com.example.readtracker.android.domain.entity.StatsDetailForTrackerPeriod
import com.example.readtracker.android.domain.entity.stats.StatsWithBookEntity
import com.example.readtracker.android.domain.repository.StatsRepository
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class GetStatsDetailsUseCase @Inject constructor(
    private val repository: StatsRepository,
    private val dateTimeManager: DateTimeManager
) {
    suspend operator fun invoke(
        date: Date,
        periodTab: PeriodTab,
        selectedBarIndex: Int? // null = выбрано всё, Int = конкретный столбик
    ): StatsDetailForTrackerPeriod {

        // Инициализируем календарь в самом начале, чтобы он был доступен везде!
        val calendar = Calendar.getInstance().apply { time = date }

        // 1. Получаем общие границы для запроса в БД (за всю неделю, месяц или год)
        val (startTimestamp, endTimestamp) = dateTimeManager.getPeriodBounds(date, periodTab)

        // 2. Делаем ОДИН запрос в базу данных за весь период
        val rawActivities = repository.getActivitiesForPeriod(startTimestamp, endTimestamp)

        // 3. Логика фильтрации данных для списка сессий (activities) и верхних метрик
        var targetActivities: List<StatsWithBookEntity> = rawActivities

        if (periodTab == PeriodTab.CALENDAR) {
            // А) ЕСЛИ ЭТО КАЛЕНДАРЬ: Фильтруем строго за один выбранный день месяца
            val currentDayBounds = dateTimeManager.getStartAndEndOfDay(calendar)
            targetActivities = rawActivities.filter {
                it.stats.timestamp in currentDayBounds.first..currentDayBounds.second
            }
        } else if (selectedBarIndex != null) {
            // Б) ЕСЛИ ВЫБРАН СТОЛБИК НА ГРАФИКЕ (Для DAYS, WEEKS, MONTHS): Вычисляем его точные границы
            val stepCal = Calendar.getInstance().apply { timeInMillis = startTimestamp }
            when (periodTab) {
                PeriodTab.DAYS -> stepCal.add(Calendar.DAY_OF_MONTH, selectedBarIndex)
                PeriodTab.WEEKS -> stepCal.add(Calendar.WEEK_OF_YEAR, selectedBarIndex)
                PeriodTab.MONTHS -> stepCal.add(Calendar.MONTH, selectedBarIndex)
                else -> {}
            }
            val barStart = stepCal.timeInMillis

            when (periodTab) {
                PeriodTab.DAYS -> stepCal.add(Calendar.DAY_OF_MONTH, 1)
                PeriodTab.WEEKS -> stepCal.add(Calendar.WEEK_OF_YEAR, 1)
                PeriodTab.MONTHS -> stepCal.add(Calendar.MONTH, 1)
                else -> {}
            }
            val barEnd = stepCal.timeInMillis

            // Оставляем только те сессии, которые попали в этот столбик
            targetActivities = rawActivities.filter { it.stats.timestamp in barStart until barEnd }
        }
        // В противном случае (selectedBarIndex == null), targetActivities так и остается равен rawActivities (вся неделя/месяц)

        // 4. Считаем метрики (Они теперь динамические и точные!)
        val totalMinutes = targetActivities.sumOf { it.stats.durationMinutes }
        val hours = totalMinutes / 60
        val remainingMinutes = totalMinutes % 60
        val totalTimeStr = String.format("%d:%02d:00", hours, remainingMinutes)

        val totalPagesStr = targetActivities.sumOf { it.stats.pagesRead }.toString()
        val totalBooksStr = targetActivities.map { it.book.id }.distinct().size.toString()

        // 5. Расчет высоты ВСЕХ столбиков для графика (график всегда рисуется целиком)
        val chartHeights = mutableListOf<Float>()

        if (periodTab != PeriodTab.CALENDAR) {
            val barCount = if (periodTab == PeriodTab.MONTHS) 12 else 7
            val stepCal = Calendar.getInstance().apply { timeInMillis = startTimestamp }
            val rawPagesPerBar = mutableListOf<Int>()

            for (i in 0 until barCount) {
                val chunkStart = stepCal.timeInMillis
                when (periodTab) {
                    PeriodTab.DAYS -> stepCal.add(Calendar.DAY_OF_MONTH, 1)
                    PeriodTab.WEEKS -> stepCal.add(Calendar.WEEK_OF_YEAR, 1)
                    PeriodTab.MONTHS -> stepCal.add(Calendar.MONTH, 1)
                    else -> {}
                }
                val chunkEnd = stepCal.timeInMillis

                val sumPages = rawActivities.filter { it.stats.timestamp in chunkStart until chunkEnd }.sumOf { it.stats.pagesRead }
                rawPagesPerBar.add(sumPages)
            }

            val maxPages = rawPagesPerBar.maxOrNull() ?: 1
            for (pages in rawPagesPerBar) {
                chartHeights.add(if (maxPages > 0) pages.toFloat() / maxPages.toFloat() else 0.0f)
            }
        }

        // 6. Сет активных дней для календаря (всегда по полной выборке из БД)
        val tempCal = Calendar.getInstance()
        val activeDays = rawActivities.map {
            tempCal.timeInMillis = it.stats.timestamp
            tempCal.get(Calendar.DAY_OF_MONTH)
        }.toSet()

        return StatsDetailForTrackerPeriod(
            selectedBarIndex = selectedBarIndex ?: -1, // -1 означает, что на графике ничего не подсвечено
            totalTimeStr = totalTimeStr,
            totalPagesStr = totalPagesStr,
            totalBooksStr = totalBooksStr,
            chartHeights = chartHeights,
            activities = targetActivities.map { it.toDomain() }, // Маппим отфильтрованные сессии в UI-модели
            selectedDay = if (periodTab == PeriodTab.CALENDAR) calendar.get(Calendar.DAY_OF_MONTH) else null,
            activeDays = activeDays
        )
    }
}