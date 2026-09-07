package com.example.readtracker.android.core

import com.example.readtracker.android.domain.entity.PeriodTab
import java.util.Calendar
import java.util.Date

class DateTimeManager {
    // Возвращает точные границы (Start, End) в зависимости от выбранного таба и даты
    fun getPeriodBounds(date: Date, periodTab: PeriodTab): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply { time = date }
        return when (periodTab) {
            PeriodTab.DAYS -> {
                // Если нужно смотреть ОДИН конкретный выбранный день:
                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val diffToMonday = if (currentDayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDayOfWeek
                calendar.add(Calendar.DAY_OF_MONTH, diffToMonday)
                val start = getStartOfDayInMillis(calendar)

                // Сдвигаем на Воскресенье (конец недели)
                calendar.add(Calendar.DAY_OF_MONTH, 6)
                val end = getEndOfDayInMillis(calendar)

                start to end
            }
            PeriodTab.WEEKS -> {
                // Границы текущей недели (Пн 00:00:00 - Вс 23:59:59)
                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val diffToSunday = if (currentDayOfWeek == Calendar.SUNDAY) 0 else Calendar.SATURDAY - currentDayOfWeek + 1
                calendar.add(Calendar.DAY_OF_MONTH, diffToSunday)
                val end = getEndOfDayInMillis(calendar)

                // Отматываем на 7 недель назад (включая текущую), чтобы найти Понедельник первой недели на графике
                calendar.time = date // Сбрасываем на исходную
                val diffToMonday = if (currentDayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDayOfWeek
                calendar.add(Calendar.DAY_OF_MONTH, diffToMonday) // Встали на Пн текущей недели
                calendar.add(Calendar.WEEK_OF_YEAR, -6) // Откатились на 6 недель назад
                val start = getStartOfDayInMillis(calendar)

                start to end
            }
            PeriodTab.MONTHS -> {
                // Границы блока из 7 недель (для графика), либо текущего месяца.
                // Сделаем границы текущего месяца для простоты, либо года, как у тебя:
                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = getStartOfDayInMillis(calendar)

                calendar.set(Calendar.MONTH, Calendar.DECEMBER)
                calendar.set(Calendar.DAY_OF_MONTH, 31)
                val end = getEndOfDayInMillis(calendar)
                start to end
            }
            PeriodTab.CALENDAR -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = getStartOfDayInMillis(calendar)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = getEndOfDayInMillis(calendar)
                start to end
            }
        }
    }

    fun getStartAndEndOfDay(calendar: Calendar): Pair<Long, Long> {
        val start = getStartOfDayInMillis(calendar)
        val end = getEndOfDayInMillis(calendar)
        return start to end
    }

    private fun getStartOfDayInMillis(cal: Calendar): Long {
        return cal.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun getEndOfDayInMillis(cal: Calendar): Long {
        return cal.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
}