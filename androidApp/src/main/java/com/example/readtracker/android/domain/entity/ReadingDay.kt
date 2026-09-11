package com.example.readtracker.android.domain.entity

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.readtracker.android.domain.entity.stats.ReadStat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

data class ReadingDay(
    val dayNumber: Int,      // Номер дня (например, 15, 16, 17...)
    val isRead: Boolean,     // Был ли зафиксирован факт чтения
    val isToday: Boolean     // Текущий ли это день (чтобы выделить его визуально)
){
    companion object{
        @RequiresApi(Build.VERSION_CODES.O)
        fun generateWeekDays(
            currentTimestamp: Long,
            statsList: List<ReadStat>
        ): List<ReadingDay> {
            // 1. Переводим текущий таймстамп в LocalDate (работаем в системной таймзоне)
            val zoneId = ZoneId.systemDefault()
            val today = Instant.ofEpochMilli(currentTimestamp).atZone(zoneId).toLocalDate()

            // 2. Находим даты начала (понедельник) и конца (воскресенье) текущей недели
            val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

            // 3. Собираем сет уникальных дней (LocalDate), в которые пользователь ХОТЯ БЫ ЧТО-ТО ЧИТАЛ
            // Мы фильтруем список, чтобы учитывать только те записи, где реально прочитана хотя бы 1 страница
            val readDates: Set<LocalDate> = statsList
                .filter { it.pagesRead > 0 }
                .map { Instant.ofEpochMilli(it.timestamp).atZone(zoneId).toLocalDate() }
                .toSet()

            // 4. Генерируем фиксированный список ровно из 7 дней текущей недели
            return (0..6).map { dayOffset ->
                val dateForDay = monday.plusDays(dayOffset.toLong()) // Идем по порядку: Пн, Вт, Ср...

                ReadingDay(
                    dayNumber = dateForDay.dayOfMonth,          // Берем число месяца (например, 15)
                    isRead = readDates.contains(dateForDay),    // Проверяем, читал ли пользователь в этот день
                    isToday = dateForDay.isEqual(today)         // Проверяем, совпадает ли день с сегодняшним
                )
            }
        }
    }
}